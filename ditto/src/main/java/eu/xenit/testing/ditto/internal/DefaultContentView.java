package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.model.ContentData;
import eu.xenit.testing.ditto.api.ContentView;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.content.InternalContentData;
import eu.xenit.testing.ditto.internal.record.RecordLogEntry;
import eu.xenit.testing.ditto.internal.repository.ContentRepository;
import eu.xenit.testing.ditto.internal.repository.Cursor;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultContentView implements ContentView {

    private final ContentRepository repository;
    private final Cursor cursor;

//    private final HashMap<String, AnnotatedContentData> contentUrlMap = new LinkedHashMap<>();
//    private final HashMap<String, InternalContentData> contentUrlMap = new LinkedHashMap<>();
//    private final HashMap<NodeReference, Map<QName, InternalContentData>> contentNodeMap = new LinkedHashMap<>();

//    public DefaultContentView(Cursor cursor) {
//
//        this.process(cursor.getHead());
//    }

    public DefaultContentView(ContentRepository repository, Cursor cursor) {
        this.repository = repository;
        this.cursor = cursor;
    }

    @Override
    public boolean exists(String contentUrl) {
        return this.repository.exists(contentUrl, this.cursor);
    }

    @Override

    public Optional<InputStream> getContent(String contentUrl) {

        return this.repository.getContent(contentUrl, this.cursor)
                .map(contentData -> contentData.getContentDelegate().get());
    }

    @Override
    public Optional<InputStream> getContent(NodeReference nodeRef) {
        return this.getContent(nodeRef, Content.CONTENT);
    }


    @Override
    public Optional<InputStream> getContent(NodeReference nodeRef, QName property) {
        return this.repository.getContent(nodeRef, property, this.cursor)
                .map(contentData -> contentData.getContentDelegate().get());
    }

}
