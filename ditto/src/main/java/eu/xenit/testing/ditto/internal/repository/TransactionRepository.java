package eu.xenit.testing.ditto.internal.repository;

import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.record.RecordChain;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionRepository implements TransactionProcessor {

    private final HashMap<Long, RecordTuple<Transaction>> txnBySeqId = new LinkedHashMap<>();
//    private final LinkedHashMap<String, Transaction> txnByChangeId = new LinkedHashMap<>();

//    @Override
//    public void process(Long recordId, Transaction txn) {
//        // txn.getId() should map to Transaction
//        // but given a certain recordId, the txn can be different
//    }

    @Override
    public void process(long recordId, RecordChain chain, Transaction txn) {
        // store the updated/deleted node + record-id for a given node-id
        RecordTuple<Transaction> root = this.txnBySeqId
                .computeIfAbsent(txn.getId(), id -> new RecordTuple<>(0L, null, true));

        // Given that transactions SHOULD NEVER get updated - we can just add this to the root node ?!
        // sanity check - we can disable this later
        assert root == root.walk(chain.iterator());

        root.addChild(new RecordTuple<>(recordId, txn));
    }

    public Optional<Transaction> getTransaction(long txnId, Cursor cursor) {
        RecordTuple<Transaction> record = this.txnBySeqId.get(txnId);
        if (record == null) {
            return Optional.empty();
        }

        // walk from the root to our record
        record = record.walk(cursor.chain().iterator());

        if (record.data == null || record.deleted) {
            return Optional.empty();
        }

        return Optional.of(record.data);
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
