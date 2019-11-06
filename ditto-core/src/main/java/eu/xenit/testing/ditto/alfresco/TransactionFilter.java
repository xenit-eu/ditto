package eu.xenit.testing.ditto.alfresco;

import java.util.function.Predicate;

public class TransactionFilter {

    public static Predicate<Transaction> minTxnId(long minTxnId) {
        return transaction -> transaction.getId() >= minTxnId;
    }

    public static Predicate<Transaction> maxTxnId(long maxTxnId) {
        return transaction -> transaction.getId() < maxTxnId;
    }
}
