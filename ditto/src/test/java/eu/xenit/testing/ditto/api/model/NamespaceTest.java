package eu.xenit.testing.ditto.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NamespaceTest {

    @Test
    void createNamespace() {
        Namespace ns = Namespace.createNamespace("foo", "bar", "baz");

        assertThat(ns.getNamespace()).isEqualTo("foo");
        assertThat(ns.getPrimaryPrefix()).isPresent().hasValue("bar");
        assertThat(ns.getPrefixes()).contains("bar", "baz");
    }

    @Test
    void namespaceCanBeNull() {
        Namespace ns = Namespace.createNamespace(null);

        assertThat(ns.getNamespace()).isEqualTo("");
        assertThat(ns.getPrimaryPrefix()).isNotPresent();
        assertThat(ns.getPrefixes()).isEmpty();
    }

    @Test
    void equality() {
        Namespace ns1 = Namespace.createNamespace("foo", "bar");
        Namespace ns2 = Namespace.createNamespace("foo");
        Namespace ns3 = Namespace.createNamespace("foo", "baz");

        Namespace ns4 = Namespace.createNamespace("baz", "bar");

        assertThat(ns1)
                .isEqualTo(ns2)
                .isEqualTo(ns3)
                .isNotEqualTo(ns4);
    }

    @Test
    void checkHashcode() {
        Namespace ns1 = Namespace.createNamespace("foo", "bar");
        Namespace ns2 = Namespace.createNamespace("foo");
        Namespace ns3 = Namespace.createNamespace("foo", "baz");

        assertThat(ns1.hashCode())
                .isEqualTo(ns2.hashCode())
                .isEqualTo(ns3.hashCode());
    }
}