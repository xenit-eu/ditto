package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.internal.repository.Cursor;
import eu.xenit.testing.ditto.internal.record.RecordLogEntry;
import eu.xenit.testing.ditto.internal.repository.TransactionRepository;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultTransactionView implements TransactionView {

    private final TransactionRepository transactionRepository;
    private final Cursor cursor;

    public DefaultTransactionView(TransactionRepository transactionRepository, Cursor cursor) {
        this.transactionRepository = transactionRepository;
        this.cursor = cursor;
    }

    public long getLastTxnId() {
        return this.getLastTxn().map(Transaction::getId).orElse(-1L);
    }

    private Optional<Transaction> getLastTxn() {
        return this.transactionRepository.stream(this.cursor).reduce((t1, t2) -> t2);
    }

    @Override
    public Optional<Transaction> getTransactionById(long id) {
        return this.transactionRepository.getTransaction(id, this.cursor);
    }

    @Override
    public Optional<Transaction> getTransactionByUuid(String uuid) {
        return Optional.empty();
    }

    public Stream<Transaction> stream() {
        return this.transactionRepository.stream(this.cursor);
    }
}
