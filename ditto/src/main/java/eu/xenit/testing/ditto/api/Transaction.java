package eu.xenit.testing.ditto.api;

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

        static <T> Predicate<T> alwaysTrue() {
            return (var0) -> true;
        }

        public static Predicate<Transaction> minTxnId(Long minTxnId) {
            if (minTxnId == null) {
                return alwaysTrue();
            }

            return transaction -> transaction.getId() >= minTxnId;
        }

        public static Predicate<Transaction> fromCommitTime(Long fromCommitTime) {
            if (fromCommitTime == null) {
                return alwaysTrue();
            }

            return transaction -> transaction.getCommitTimeMs() >= fromCommitTime;
        }

        public static Predicate<Transaction> maxTxnId(Long maxTxnId) {
            if (maxTxnId == null) {
                return alwaysTrue();
            }

            return transaction -> transaction.getId() < maxTxnId;
        }

        public static Predicate<Transaction> toCommitTime(Long toCommitTime) {
            if (toCommitTime == null) {
                return alwaysTrue();
            }

            return transaction -> transaction.getCommitTimeMs() < toCommitTime;
        }
    }
}
