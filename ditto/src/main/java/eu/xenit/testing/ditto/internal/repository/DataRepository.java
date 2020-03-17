package eu.xenit.testing.ditto.internal.repository;

import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.record.RecordChain;
import eu.xenit.testing.ditto.internal.record.RecordLog;
import eu.xenit.testing.ditto.internal.record.RecordLogEntry;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import lombok.Getter;

public class DataRepository {

    private final RecordLog<Transaction> records;

    @Getter
    private final TransactionRepository txnRepository;

    @Getter
    private final NodeRepository nodeRepository;

    @Getter
    private final ContentRepository contentRepository;

    public DataRepository() {
        this.records = new RecordLog<>();

        this.nodeRepository = new NodeRepository();
        this.txnRepository = new TransactionRepository();
        this.contentRepository = new ContentRepository();
    }

    public Cursor getRootCursor() {
        return new Cursor(this.records.getRoot());
    }

    public Cursor process(Cursor cursor, Stream<Transaction> transactions) {
        RecordLogEntry<Transaction> head = transactions.reduce(cursor.getHead(), (parent, txn) -> {
            return new RecordLogEntry<>(parent, txn, this::processTransactionData);
        }, PARALLEL_STREAMS_NOT_SUPPORTED);

        return new Cursor(head);
    }

    private void processTransactionData(Long recordId, RecordChain recordChain, Transaction transaction) {
        this.txnRepository.process(recordId, recordChain, transaction);
        this.nodeRepository.process(recordId, recordChain, transaction);
        this.contentRepository.process(recordId, recordChain, transaction);
    }

    private final BinaryOperator<RecordLogEntry<Transaction>> PARALLEL_STREAMS_NOT_SUPPORTED = (l1, l2) -> {
        throw new UnsupportedOperationException("parallel streams not supported");
    };
}
