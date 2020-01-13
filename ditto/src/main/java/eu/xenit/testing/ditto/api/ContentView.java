package eu.xenit.testing.ditto.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

public interface ContentView {

    boolean exists(String contentUrl);

    Optional<InputStream> getContent(String contentUrl);

    Optional<InputStream> getContent(NodeReference nodeRef);
    Optional<InputStream> getContent(NodeReference nodeRef, String property);

}
