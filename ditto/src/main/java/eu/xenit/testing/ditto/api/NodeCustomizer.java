package eu.xenit.testing.ditto.api;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface NodeCustomizer {

    Node build();

    NodeCustomizer mimetype(String mimetype);

    NodeCustomizer size(long size);

    NodeReference getNodeRef();

    NodeCustomizer properties(Map<String, Serializable> properties);

    NodeCustomizer property(String key, String value);

    NodeCustomizer aspects(Set<String> aspects);

    NodeCustomizer aspect(String aspect);

    NodeCustomizer name(String name);

    NodeCustomizer content(String content);

    NodeCustomizer content(String content, Charset charset);

    NodeCustomizer content(byte[] content);

    NodeCustomizer callback(Consumer<Node> callback);

    NodeCustomizer createNamedReference(String name);

    NodeCustomizer createNamedReference();

    NodeCustomizer storeRefProtocol(String storeRefProtocol);

    NodeCustomizer storeRefIdentifier(String storeRefIdentifier);

    NodeCustomizer uuid(String uuid);

    NodeCustomizer isDocument(boolean isDocument);

    NodeCustomizer type(String type);

    NodeCustomizer charset(Charset charset);

    long getNodeId();

    String storeRefProtocol();

    String storeRefIdentifier();

    String uuid();

    boolean isDocument();

    String mimetype();

    long size();

    String type();

    Map<String, Serializable> getProperties();

    byte[] content();

    Charset charset();

    Set<String> getAspects();
}
