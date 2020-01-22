package eu.xenit.testing.ditto.api.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public interface Transaction {

    long getId();
    String getChangeId();

    long getVersion();
    long getServerId();

    long getCommitTimeMs();

    Set<Node> getUpdated();
    Set<Node> getDeleted();

    interface Filters {

        static <T> Predicate<T> always(boolean value) {
            return (var0) -> value;
        }

        static Predicate<Transaction> minTxnId(Long minTxnId) {
            if (minTxnId == null) {
                return always(true);
            }

            return transaction -> transaction.getId() >= minTxnId;
        }

        static Predicate<Transaction> fromCommitTime(Long fromCommitTime) {
            if (fromCommitTime == null) {
                return always(true);
            }

            return transaction -> transaction.getCommitTimeMs() >= fromCommitTime;
        }

        static Predicate<Transaction> maxTxnId(Long maxTxnId) {
            if (maxTxnId == null) {
                return always(true);
            }

            return transaction -> transaction.getId() < maxTxnId;
        }

        static Predicate<Transaction> toCommitTime(Long toCommitTime) {
            if (toCommitTime == null) {
                return always(true);
            }

            return transaction -> transaction.getCommitTimeMs() < toCommitTime;
        }

        static Predicate<Transaction> containedIn(Collection<Long> txnIds)
        {
            if (txnIds == null) {
                return always(true);
            }

            if (txnIds.isEmpty()) {
                return always(false);
            }

            HashSet<Long> set = new HashSet<>(txnIds);
            return (txn) -> set.contains(txn.getId());
        }
    }
}
