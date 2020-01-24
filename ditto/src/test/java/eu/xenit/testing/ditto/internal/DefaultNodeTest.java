package eu.xenit.testing.ditto.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionContext;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DefaultNodeTest {

    @Test
    void txnId() {
        RootContext root = mock(RootContext.class);
        when(root.now()).thenReturn(Instant.now());
        when(root.nextTxnId()).thenReturn(7L);
        TransactionContext txn = new TransactionContext(root);

        Node node = DefaultNode.builder(txn).build();

        assertThat(node.getTxnId()).isEqualTo(7L);
    }

}