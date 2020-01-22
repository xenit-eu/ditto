package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.model.ContentData;
import eu.xenit.testing.ditto.api.ContentView;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.content.InternalContentData;
import eu.xenit.testing.ditto.internal.mvcc.Cursor;
import eu.xenit.testing.ditto.internal.mvcc.RecordLogEntry;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultContentView implements ContentView {


//    private final HashMap<String, AnnotatedContentData> contentUrlMap = new LinkedHashMap<>();
    private final HashMap<String, InternalContentData> contentUrlMap = new LinkedHashMap<>();
    private final HashMap<NodeReference, Map<QName, InternalContentData>> contentNodeMap = new LinkedHashMap<>();

    public DefaultContentView(Cursor<Transaction> cursor) {

        this.process(cursor.getHead());
    }

    @Override
    public boolean exists(String contentUrl) {
        return this.contentUrlMap.containsKey(contentUrl);
    }

    @Override
    public Optional<InputStream> getContent(String contentUrl) {

        InternalContentData contentData = this.contentUrlMap.getOrDefault(contentUrl, null);
        if (contentData == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(contentData.getContentDelegate().get());
    }

    @Override
    public Optional<InputStream> getContent(NodeReference nodeRef) {
        return this.getContent(nodeRef, Content.CONTENT);
    }


    @Override
    public Optional<InputStream> getContent(NodeReference nodeRef, QName property) {
        Map<QName, InternalContentData> contentDataMap = this.contentNodeMap.get(nodeRef);
        if (contentDataMap == null) {
            return Optional.empty();
        }

        InternalContentData contentData = contentDataMap.get(property);
        if (contentData == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(contentData.getContentDelegate().get());
    }


    private void process(RecordLogEntry<Transaction> head) {
        this.processRecursive(head);
    }

    private void processRecursive(RecordLogEntry<Transaction> record) {

        Objects.requireNonNull(record, "record cannot be null");

        RecordLogEntry<Transaction> parent = record.getParent();
        if (parent == null) {
            return;
        }

        processRecursive(parent);

        this.process(record.getData());
    }

    private void process(Transaction txn) {
        log.debug("Replaying {} - with {} writes and {} deletes", txn, txn.getUpdated().size(), txn.getDeleted().size());

        txn.getUpdated().forEach(this::add);
        txn.getDeleted().forEach(this::delete);
    }

    private void add(Node node) {
        node.getProperties().forEach((key, value) -> {
            if (value instanceof InternalContentData) {
                InternalContentData contentData = (InternalContentData) value;

                // update the content-url-map
                InternalContentData conflict = this.contentUrlMap.putIfAbsent(
                                contentData.getContentUrl(), contentData);
                if (conflict != null) {
                    // TODO add an indirection, taking into account multiple nodes can map to the same content
                    throw new UnsupportedOperationException("Conflict: content-url already exists");
                }

                // update the content-node-map
                Map<QName, InternalContentData> nodeContentDataMap = this.contentNodeMap
                        .computeIfAbsent(node.getNodeRef(), (nodeRef) -> new HashMap<>());
                InternalContentData oldContent = nodeContentDataMap.putIfAbsent(key, contentData);
                if (oldContent != null) {
                    this.orphaneContentData(oldContent);
                }
            }
        });
    }

    private void orphaneContentData(InternalContentData contentData) {
        log.warn("TODO - orphaned %s - should be cleaned up ?!", contentData.getContentUrl());

        // 1. contentUrlMap value object should probably be extended, so it can be linked back
        // to the node it belongs too
        // 2. take into account that the link-back should probably be plural, because multiple
        // nodes could actually refer to the same content
    }

    private void delete(Node node) {
        node.getProperties().forEach((key, value) -> {
            if (value instanceof ContentData) {
                ContentData contentData = (ContentData) value;
                this.contentUrlMap.remove(contentData.getContentUrl());
            }
            this.contentNodeMap.remove(node.getNodeRef());
        });
    }

}
