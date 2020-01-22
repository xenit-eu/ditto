package eu.xenit.testing.ditto.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.testing.ditto.api.model.Node;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DefaultTransactionTest {

    @Test
    void testTransactionUpdatesKeepsInsertionOrder() {
        RootContext root = mock(RootContext.class);
        when(root.now()).thenReturn(Instant.now());

        DefaultTransaction txn = DefaultTransaction.builder(root)
                .addNode(node -> node.name("1"))
                .addNode(node -> node.name("2"))
                .addNode(node -> node.name("3"))
                .addNode(node -> node.name("4"))
                .addNode(node -> node.name("5"))
                .build();

        assertThat(txn.getUpdated().stream().map(Node::getName))
                .containsSequence("1", "2", "3", "4", "5");



    }
}