package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.model.Transaction;
import java.util.Optional;
import java.util.stream.Stream;

public interface TransactionView {

    long getLastTxnId();

    Optional<Transaction> getTransactionById(long id);
    Optional<Transaction> getTransactionByUuid(String uuid);

    Stream<Transaction> stream();

    default Optional<Transaction> getLastTransaction() {
        return this.getTransactionById(this.getLastTxnId());
    }

    default long getLastCommitTimeMs() {
        return this.getLastTransaction().map(Transaction::getCommitTimeMs).orElse(-1L);
    }

}
