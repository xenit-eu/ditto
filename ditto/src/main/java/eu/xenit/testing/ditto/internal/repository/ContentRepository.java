package eu.xenit.testing.ditto.internal.repository;

import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.content.InternalContentData;
import eu.xenit.testing.ditto.internal.record.RecordChain;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

public class ContentRepository extends DataRepositoryBase implements TransactionProcessor {

    private final HashMap<String, RecordTuple<InternalContentData>> contentUrlMap = new LinkedHashMap<>();
    private final HashMap<ContentPropertyKey, RecordTuple<InternalContentData>> contentPropMap = new LinkedHashMap<>();

    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static final class ContentPropertyKey {
        private final NodeReference nodeReference;
        private final QName property;
    }

    @Override
    public void process(long recordId, RecordChain chain, Transaction txn) {
        txn.getUpdated().forEach(node -> {
            node.getProperties().forEach((key, value) -> {
                if (value instanceof InternalContentData) {
                    InternalContentData contentData = (InternalContentData) value;

                    this.store(chain, recordId, contentData)
                            .withIndex(this.contentUrlMap, tuple -> tuple.data.getContentUrl())
                            .withIndex(this.contentPropMap, tuple -> new ContentPropertyKey(node.getNodeRef(), key));
                }
            });
        });

        txn.getDeleted().forEach(delete -> {
            throw new UnsupportedOperationException("not implemented");
        });
    }

    public boolean exists(String contentUrl, Cursor cursor) {
        return this.getContent(contentUrl, cursor).isPresent();
    }

    public Optional<InternalContentData> getContent(String contentUrl, Cursor cursor) {
        return this.load(this.contentUrlMap, contentUrl, cursor.chain());
    }

    public Optional<InternalContentData> getContent(NodeReference nodeRef, QName property, Cursor cursor) {
        return this.load(this.contentPropMap, new ContentPropertyKey(nodeRef, property), cursor.chain());
    }
}
