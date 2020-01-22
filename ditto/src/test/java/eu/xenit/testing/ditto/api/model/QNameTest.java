package eu.xenit.testing.ditto.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QNameTest {


    @Test
    void createQName() {
        Namespace contentNamespace = Namespace.createNamespace("http://www.alfresco.org/model/content/1.0", "cm");
        QName qname = QName.createQName(contentNamespace, "folder");

        assertThat(qname).isNotNull();
        assertThat(qname.toString()).isEqualTo("{http://www.alfresco.org/model/content/1.0}folder");
        assertThat(qname.toPrefixString()).isEqualTo("cm:folder");
    }

}