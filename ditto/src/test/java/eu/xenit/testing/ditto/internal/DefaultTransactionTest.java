package eu.xenit.testing.ditto.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionBuilder;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DefaultTransactionTest {

    @Test
    void testTransactionUpdatesKeepsInsertionOrder() {
        RootContext root = mock(RootContext.class);
        when(root.now()).thenReturn(Instant.now());

        TransactionBuilder builder = DefaultTransaction.builder(root);
        builder.addNode(node -> node.name("1"));
        builder.addNode(node -> node.name("2"));
        builder.addNode(node -> node.name("3"));
        builder.addNode(node -> node.name("4"));
        builder.addNode(node -> node.name("5"));

        DefaultTransaction txn = builder.build();

        assertThat(txn.getUpdated().stream().map(Node::getName))
                .containsSequence("1", "2", "3", "4", "5");

    }

    @Test
    void testGetNodeByNodeRef_fromTxnCustomizer() {
        MockRootContext root = new MockRootContext();

        TransactionBuilder txn1 = DefaultTransaction.builder(root);
        txn1.addNode(node -> {
            node.name("foo.doc");
            node.uuid("abc-123");
        });

        TransactionBuilder txn2 = DefaultTransaction.builder(root);
        Node node = txn2.getNodeByNodeRef("workspace://SpacesStore/abc-123");

        assertThat(node).isNotNull();
        assertThat(node.getName()).isEqualTo("foo.doc");
    }
}