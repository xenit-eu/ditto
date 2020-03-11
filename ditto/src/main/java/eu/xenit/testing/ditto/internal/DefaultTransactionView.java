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

//    private final LinkedHashMap<Long, Transaction> txnBySeqId = new LinkedHashMap<>();
//    private final LinkedHashMap<String, Transaction> txnByChangeId = new LinkedHashMap<>();

    public DefaultTransactionView(TransactionRepository transactionRepository, Cursor cursor) {
        this.transactionRepository = transactionRepository;
        this.cursor = cursor;
//        this.process(this.cursor.getHead());
    }

//    private void process(RecordLogEntry<Transaction> head) {
//
//        // TODO
//        // We need to walk the record-log from head back to the tail
//        // The natural way to do this, would involve recursion: apply(entry) -> apply(parent)
//        // Given that the JVM still lacks TCO (tail call optimisation), large'ish logs (10k records?)
//        // will result into a stack overflow.
//        // To avoid those very deep call stacks, converting the recursion into a loop with
//        // continuation-passing style should work.
//        //
//        // This technique is called 'trampolines' or 'trampolining'.
//        //
//        // References:
//        // * https://blog.logrocket.com/using-trampolines-to-manage-large-recursive-loops-in-javascript-d8c9db095ae3/
//        // * https://www.uraimo.com/2016/05/05/recursive-tail-calls-and-trampolines-in-swift/
//        // * https://en.wikipedia.org/wiki/Continuation-passing_style
//        this.processRecursive(head);
//    }
//
//    private void processRecursive(RecordLogEntry<Transaction> record) {
//
//        Objects.requireNonNull(record, "record cannot be null");
//
//        RecordLogEntry<Transaction> parent = record.getParent();
//        if (parent == null) {
//            return;
//        }
//
//        processRecursive(parent);
//
//        this.addTransaction(record.getData());
//    }

    public long getLastTxnId() {
        return this.getLastTxn().map(Transaction::getId).orElse(-1L);
    }

    private Optional<Transaction> getLastTxn() {
        return this.transactionRepository.stream(this.cursor).reduce((t1, t2) -> t2);
    }

//    private void addTransaction(Transaction txn) {
//        Objects.requireNonNull(txn, "txn cannot be null");
//        Objects.requireNonNull(txn.getChangeId(), "txn.getChangeId() cannot be null");
//
//        log.debug("Replaying {}", txn);
//
//        // transaction sequence ids should be monotonically increasing
//        if (txn.getId() <= this.lastTxnId) {
//            String msg = String.format("Invalid transaction id for %s - expected value > %s", txn, this.lastTxnId);
//            throw new IllegalArgumentException(msg);
//        }
//
//        this.txnBySeqId.put(txn.getId(), txn);
//
//        Transaction conflict = this.txnByChangeId.putIfAbsent(txn.getChangeId(), txn);
//        if (conflict != null) {
//            String msg = String.format("Transaction %s has a conflict with %s", txn, conflict);
//            throw new IllegalArgumentException(msg);
//        }
//
//        this.lastTxnId = txn.getId();
//    }

    @Override
    public Optional<Transaction> getTransactionById(long id) {
        return this.transactionRepository.getTransaction(id, this.cursor);
    }

    @Override
    public Optional<Transaction> getTransactionByUuid(String uuid) {
//        return Optional.ofNullable(this.txnByChangeId.get(uuid));
        return Optional.empty();
    }

    public Stream<Transaction> stream() {
        return this.transactionRepository.stream(this.cursor);
    }
}
