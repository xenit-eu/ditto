package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.model.Namespace;
import eu.xenit.testing.ditto.api.model.QName;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class DictionaryService {

    private Map<String, Namespace> namespaceByUriRegistry = new HashMap<>();
    private Map<String, Namespace> namespaceByPrefix = new HashMap<>();

    public void registerNamespace(Namespace... namespaces) {
        Stream.of(namespaces).forEach(ns -> {
            if (ns == null) {
                throw new IllegalArgumentException("argument cannot be null");
            }

            Namespace existing = this.namespaceByUriRegistry.putIfAbsent(ns.getNamespace(), ns);
            if (existing != null) {
                // should we allow re-registering namespaces ? Maybe to register additional prefixes ?
                throw new UnsupportedOperationException("namespace is already registered: " + ns.getNamespace());
            }

            ns.getPrefixes().forEach(prefix -> {
                Namespace existingPrefix = this.namespaceByPrefix.putIfAbsent(prefix, ns);
                if (existingPrefix != null) {
                    // should we allow re-registering prefixes ?
                    throw new IllegalArgumentException("prefix is already registered: " + prefix);
                }
            });
        });
    }


    public Namespace lookupNamespace(String fullNamespace) {
        Objects.requireNonNull(fullNamespace, "Argument 'fullNamespace' is required");

        Namespace namespace = this.namespaceByUriRegistry.get(fullNamespace);
        if (namespace == null) {
            throw new IllegalStateException(String.format("Namespace %s is not registered", fullNamespace));
        }

        return namespace;
    }

    public Namespace lookupNamespacePrefix(String prefix) {
        Objects.requireNonNull(prefix, "Argument 'namespacePrefix' is required");

        Namespace namespace = this.namespaceByPrefix.get(prefix);
        if (namespace == null) {
            throw new IllegalStateException(String.format("Namespace prefix '%s' is not registered", prefix));
        }

        return namespace;
    }

    public QName resolveQName(String qname) {
        if (qname == null || qname.length() == 0) {
            throw new IllegalArgumentException("value parameter is mandatory");
        }

        if (qname.charAt(0) == (QName.NAMESPACE_BEGIN)) {
            int namespaceEnd = qname.indexOf(QName.NAMESPACE_END, 1);
            String namespaceUri = qname.substring(1, namespaceEnd);

            Namespace ns = this.lookupNamespace(namespaceUri);
            return QName.createQName(ns, qname.substring(namespaceEnd + 1));
        }

        int prefixIndex = qname.indexOf(QName.NAMESPACE_PREFIX);
        if (prefixIndex == -1) {
            // there is no namespace
            String msg = String.format("Argument qname '%s' does not contain a namespace", qname);
            throw new IllegalArgumentException(msg);
        }

        String prefix = qname.substring(0, prefixIndex);
        Namespace ns = this.lookupNamespacePrefix(prefix);
        String localname = qname.substring(prefixIndex + 1);

        return QName.createQName(ns, localname);
    }
}
