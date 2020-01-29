package eu.xenit.testing.ditto.api.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class QName {

    private final Namespace namespace;
    private final String localName;

    public static final char NAMESPACE_PREFIX = ':';
    public static final char NAMESPACE_BEGIN = '{';
    public static final char NAMESPACE_END = '}';

    private QName(Namespace namespace, String localName) {

        this.namespace = namespace;
        this.localName = localName;
    }

    public static QName createQName(Namespace namespace, String localName) {
        return new QName(namespace, localName);
    }

//    public static QName createQName

    /**
     * Gets the name
     *
     * @return the name
     */
    public String getLocalName() {
        return this.localName;
    }


    /**
     * Gets the namespace
     *
     * @return the namespace (empty string when not specified, but never null)
     */
    public String getNamespaceURI() {
        return this.namespace.getNamespace();
    }

    @Override
    public String toString() {
        return NAMESPACE_BEGIN + this.getNamespaceURI() + NAMESPACE_END + localName;
    }

    /**
     * Render string representation of QName using format:
     *
     * <code>prefix:name</code>
     *
     * @return the string representation
     */
    public String toPrefixString() {
        return this.namespace.getPrimaryPrefix()
                .map(prefix -> prefix + NAMESPACE_PREFIX + localName)
                .orElse(localName);
    }

}
