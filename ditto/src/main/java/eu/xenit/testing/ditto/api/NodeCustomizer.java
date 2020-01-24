package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.QName;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface NodeCustomizer {

    // This should not be part of the public API ?
    // Node build();

    NodeReference nodeRef();

    Map<QName, Serializable> properties();
    NodeCustomizer properties(Map<QName, Serializable> properties);
    NodeCustomizer property(String key, Serializable value);
    NodeCustomizer property(QName key, Serializable value);
    NodeCustomizer mlProperty(QName key, String value);
    NodeCustomizer mlProperty(QName key, Locale locale, String value);

    Set<QName> aspects();
    NodeCustomizer aspects(Set<String> aspects);
    NodeCustomizer aspect(String aspect);
    NodeCustomizer aspect(QName aspect);

    NodeCustomizer name(String name);

    NodeCustomizer content(String content);
    NodeCustomizer content(String content, Charset charset);
    NodeCustomizer content(byte[] content);

    NodeCustomizer callback(Consumer<Node> callback);

    NodeCustomizer createNamedReference(String name);
    NodeCustomizer createNamedReference();

    String storeRefProtocol();
    NodeCustomizer storeRefProtocol(String storeRefProtocol);

    String storeRefIdentifier();
    NodeCustomizer storeRefIdentifier(String storeRefIdentifier);

    NodeCustomizer uuid(String uuid);

    NodeCustomizer isDocument(boolean isDocument);

    QName type();
    NodeCustomizer type(String type);
    NodeCustomizer type(QName type);


    NodeCustomizer qname(String s);
    NodeCustomizer qname(QName s);

    NodeCustomizer charset(Charset charset);

    long nodeId();
    long txnId();


    String uuid();

    boolean isDocument();

    byte[] content();
    Charset charset();
    NodeCustomizer mimetype(String mimetype);
    String mimetype();
    NodeCustomizer size(long size);
    long size();



}
