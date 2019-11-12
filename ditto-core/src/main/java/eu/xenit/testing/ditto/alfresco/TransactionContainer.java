package eu.xenit.testing.ditto.alfresco;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionContainer {

    private final LinkedList<Transaction> transactions = new LinkedList<>();

    public TransactionContainer(Collection<Transaction> transactions)
    {
        this.transactions.addAll(transactions);
    }

    public TransactionContainer()
    {

    }

    public void add(Transaction txn) {
        Objects.requireNonNull(txn, "Argument 'txn' can not be null");
        this.transactions.add(txn);
    }

    public Optional<Transaction> getTransactionById(long txnId)
    {
        return this.transactions.stream()
                .filter(txn -> txn.getId() == txnId)
                .findFirst();
    }

    public Stream<Transaction> stream() {
        return this.transactions.stream();
    }


    public Transaction getLastTransaction() {
        return this.transactions.getLast();
    }

    public Long getLastTxnId() {
        Transaction lastTxn = this.getLastTransaction();
        return (lastTxn == null) ? null : lastTxn.getId();
    }

    public Long getLastCommitTimeMs() {
        Transaction lastTxn = this.getLastTransaction();
        return (lastTxn == null) ? null : lastTxn.getCommitTimeMs();
    }
}
