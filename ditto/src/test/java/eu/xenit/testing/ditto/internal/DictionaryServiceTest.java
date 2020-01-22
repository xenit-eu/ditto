package eu.xenit.testing.ditto.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.QName;
import org.junit.jupiter.api.Test;

class DictionaryServiceTest {

    @Test
    void registerNamespace() {
        DictionaryService dictionary = new DictionaryService();
        dictionary.registerNamespace(Content.NAMESPACE);

        assertThat(dictionary.lookupNamespace("http://www.alfresco.org/model/content/1.0"))
                .isNotNull()
                .isEqualTo(Content.NAMESPACE);

        assertThat(dictionary.lookupNamespacePrefix("cm"))
                .isNotNull()
                .isEqualTo(Content.NAMESPACE);
    }

    @Test
    void lookupNonExistingNamespaceThrows() {
        DictionaryService dictionary = new DictionaryService();
        assertThatThrownBy(() -> dictionary.lookupNamespace("http://www.alfresco.org/model/content/1.0"));
    }

    @Test
    void resolveFullQName() {
        DictionaryService dictionary = new DictionaryService();
        dictionary.registerNamespace(Content.NAMESPACE);

        QName qname = dictionary.resolveQName(Content.FOLDER.toString());

        assertThat(qname).isNotNull();
        assertThat(qname.getNamespaceURI()).isEqualTo(Content.NAMESPACE.getNamespace());
        assertThat(qname.toPrefixString()).isEqualTo("cm:folder");
    }

    @Test
    void resolvePrefixedQName() {
        DictionaryService dictionary = new DictionaryService();
        dictionary.registerNamespace(Content.NAMESPACE);

        QName qname = dictionary.resolveQName("cm:folder");

        assertThat(qname).isNotNull();
        assertThat(qname.getNamespaceURI()).isEqualTo(Content.NAMESPACE.getNamespace());
        assertThat(qname.toPrefixString()).isEqualTo("cm:folder");
    }

    @Test
    void resolveInvalidPrefixedQNameThrowsException() {
        DictionaryService dictionary = new DictionaryService();
        assertThatThrownBy(() -> dictionary.resolveQName("cm:folder"));
    }
}