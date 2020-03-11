package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.api.model.Transaction;
import java.util.Optional;
import java.util.stream.Stream;

public class DataStorageTransactionView implements TransactionView {

    @Override
    public long getLastTxnId() {
        return 0;
    }

    @Override
    public Optional<Transaction> getTransactionById(long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Transaction> getTransactionByUuid(String uuid) {
        return Optional.empty();
    }

    @Override
    public Stream<Transaction> stream() {
        return null;
    }
}
