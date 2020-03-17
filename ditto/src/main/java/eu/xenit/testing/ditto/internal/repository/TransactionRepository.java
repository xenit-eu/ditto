package eu.xenit.testing.ditto.internal.repository;

import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.record.RecordChain;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionRepository extends DataRepositoryBase implements TransactionProcessor {

    private final HashMap<Long, RecordTuple<Transaction>> txnBySeqId = new LinkedHashMap<>();

    @Override
    public void process(long recordId, RecordChain chain, Transaction txn) {

        this.store(chain, recordId, txn)
                .withIndex(this.txnBySeqId, tuple -> tuple.data.getId());
    }

    public Optional<Transaction> getTransaction(long txnId, Cursor cursor) {
        return this.load(this.txnBySeqId, txnId, cursor.chain());
    }

    public Stream<Transaction> stream(Cursor cursor) {
        // TODO does insertion order means these transactions are streamed chronologically ?!
        return this.txnBySeqId.values()
                .stream()
                .map(record -> record.walk(cursor.chain().iterator()))
                .filter(record -> !record.deleted)
                .map(record -> record.data);
    }
}
