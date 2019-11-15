package eu.xenit.testing.ditto.alfresco;

import java.util.function.Function;
import java.util.function.Predicate;

public class TransactionFilter {

    private static <T> Predicate<T> alwaysTrue() {
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
