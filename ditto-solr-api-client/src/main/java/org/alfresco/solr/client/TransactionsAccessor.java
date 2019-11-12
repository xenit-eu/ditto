package org.alfresco.solr.client;

import java.util.List;

public class TransactionsAccessor extends Transactions {

    public TransactionsAccessor(List<Transaction> transactions, Long maxTxnCommitTime, Long maxTxnId) {
        super(transactions, maxTxnCommitTime, maxTxnId);
    }
}
