package eu.xenit.testing.ditto.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.MLText;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.ParentChildNodeCollection;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionContext;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DefaultNodeTest {

    @Test
    void txnId() {
        RootContext root = mock(RootContext.class);
        when(root.now()).thenReturn(Instant.now());
        when(root.nextTxnId()).thenReturn(7L);
        TransactionContext txn = new TransactionContext(root);

        Node node = DefaultNode.builder(txn, null, null).build();

        assertThat(node.getTxnId()).isEqualTo(7L);
    }

    @Test
    void mlTextProperties() {
        RootContext root = mock(RootContext.class);
        when(root.now()).thenReturn(Instant.now());
        when(root.nextTxnId()).thenReturn(7L);
        TransactionContext txn = new TransactionContext(root);
        when(txn.defaultLocale()).thenReturn(Locale.ITALIAN);

        final String name = "My Node Name";
        final String title = "My Fantastic Title";

        Node node = DefaultNode.builder(txn, null, null)
                .property(Content.NAME, name)
                .property(Content.TITLE, MLText.create(Locale.ITALIAN, title))
                .build();

        assertThat(node.getProperties().get(Content.TITLE))
                .isPresent()
                .hasValueSatisfying(value -> assertThat(value)
                        .isExactlyInstanceOf(MLText.class)
                        .satisfies(val -> {
                            MLText mlText = (MLText) val;
                            assertThat(mlText.get(Locale.ITALIAN)).isEqualTo(title);
                            assertThat(mlText.get(Locale.CHINESE)).isNull();
                        }));
        assertThat(node.getProperties().getMLText(Content.TITLE, Locale.ITALIAN)).hasValue(title);
        assertThat(node.getProperties().getMLText(Content.TITLE, Locale.CHINA)).isNotPresent();
        assertThat(node.getProperties().get(Content.NAME)).hasValue(name);

    }

    @Test
    void parentAssocsSingle() {
        final Node[] parents = new Node[1];
        final String[] noderefs = new String[1];

        AlfrescoDataSet dataSet = AlfrescoDataSet
                .bootstrapAlfresco(Instant.now())
                .skipToTransaction(123L)
                .addTransaction(txn -> {
                    parents[0] = txn.addNode(doc -> {
                        doc.type(Content.FOLDER);
                        doc.name("foo");
                        doc.property("cm:description", "Folder description");
                    });
                })
                .skipToTransaction(456L)
                .addTransaction((txn -> {
                    txn.addNode(parents[0], doc -> {
                        doc.type(Content.CONTENT);
                        doc.name("bar.txt");
                        doc.content("bar");
                        doc.property("cm:description", "Document description");
                        noderefs[0] = doc.nodeRef().toString();
                    });
                }))
                .build();
        Optional<Node> node1 = dataSet.getNodeView().getNode(noderefs[0]);
        assertThat(node1).isPresent().hasValueSatisfying(value -> {
            assertThat(value).isInstanceOf(Node.class)
                    .satisfies(node -> {
                        ParentChildNodeCollection parentNodeCollection = node.getParentNodeCollection();
                        assertThat(parentNodeCollection.getAssociations().count()).isEqualTo(1);
                        assertThat(parentNodeCollection.getAssociations().collect(Collectors.toList())
                                .get(0).getParent()).isEqualTo(parents[0]);
                    });
        });
    }


}