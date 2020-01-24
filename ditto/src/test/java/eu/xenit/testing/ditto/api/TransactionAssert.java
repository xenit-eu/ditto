package eu.xenit.testing.ditto.api;

import static eu.xenit.testing.ditto.internal.DittoAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.model.Transaction;
import java.util.function.Consumer;
import org.assertj.core.api.AbstractAssert;

public class TransactionAssert extends AbstractAssert<TransactionAssert, Transaction> {

    public TransactionAssert(Transaction transaction) {
        super(transaction, TransactionAssert.class);
    }

    public TransactionAssert hasNodeWithId(long nodeId, Consumer<NodeAssert> callback) {

        assertThat(this.actual.getUpdated().stream().filter(n -> n.getNodeId() == nodeId).findFirst())
                .isPresent()
                .hasValueSatisfying(node -> callback.accept(assertThat(node)));

        return myself;
    }
}
