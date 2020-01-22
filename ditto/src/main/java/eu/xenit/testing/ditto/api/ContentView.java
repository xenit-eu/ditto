package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.QName;
import java.io.InputStream;
import java.util.Optional;

public interface ContentView {

    boolean exists(String contentUrl);

    Optional<InputStream> getContent(String contentUrl);

    Optional<InputStream> getContent(NodeReference nodeRef);
    Optional<InputStream> getContent(NodeReference nodeRef, QName property);

}
