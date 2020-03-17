package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.ContentView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.repository.ContentRepository;
import eu.xenit.testing.ditto.internal.repository.Cursor;
import java.io.InputStream;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultContentView implements ContentView {

    private final ContentRepository repository;
    private final Cursor cursor;

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
