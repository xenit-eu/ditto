package eu.xenit.testing.ditto.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.DefaultTransaction.DefaultTransactionBuilder;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DefaultTransactionTest {

    @Test
    void testTransactionUpdatesKeepsInsertionOrder() {
        RootContext root = mock(RootContext.class);
        when(root.now()).thenReturn(Instant.now());

        DefaultTransactionBuilder builder = DefaultTransaction.builder(root, null);
        builder.addNode(node -> node.name("1"));
        builder.addNode(node -> node.name("2"));
        builder.addNode(node -> node.name("3"));
        builder.addNode(node -> node.name("4"));
        builder.addNode(node -> node.name("5"));

        Transaction txn = builder.build();

        assertThat(txn.getUpdated().stream().map(Node::getName))
                .containsSequence("1", "2", "3", "4", "5");

    }
}