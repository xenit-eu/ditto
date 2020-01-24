package eu.xenit.testing.ditto.internal;



import static eu.xenit.testing.ditto.internal.DittoAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.TransactionAssert;
import eu.xenit.testing.ditto.api.model.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.assertj.core.api.AbstractAssert;

public class DefaultDataSetBuilderAssert extends AbstractAssert<DefaultDataSetBuilderAssert, DefaultDataSetBuilder> {

    public DefaultDataSetBuilderAssert(DefaultDataSetBuilder defaultDataSetBuilder) {
        super(defaultDataSetBuilder, DefaultDataSetBuilderAssert.class);
    }

    public DefaultDataSetBuilderAssert hasTransactionWithId(long txnId, Consumer<TransactionAssert> callback) {
        Optional<Transaction> txn = this.actual.getTransactions().stream()
                .filter(t -> t.getId() == txnId)
                .findFirst();

        assertThat(txn)
                .isPresent()
                .hasValueSatisfying(t -> {
                    assertThat(t.getId()).isEqualTo(txnId);
                    callback.accept(assertThat(t));
                });

        return myself;
    }

    public List<Transaction> getTransactions() {
        return this.actual.getTransactions();
    }
}
