package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.Node;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.Transaction;
import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.internal.mvcc.Cursor;
import eu.xenit.testing.ditto.internal.mvcc.RecordLog;
import eu.xenit.testing.ditto.internal.mvcc.RecordLogEntry;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public class DefaultAlfrescoDataSet implements AlfrescoDataSet {

    @Getter
    private TransactionView transactionView;

    @Getter
    public NodeView nodeView;

    private Cursor<Transaction> cursor;

    private final Map<String, Node> namedReferences;

    DefaultAlfrescoDataSet(DefaultDataSetBuilder builder) {

        RecordLog<Transaction> txnLog = new RecordLog<>();
        RecordLogEntry<Transaction> head = txnLog.process(builder.getTransactions().stream());

        this.cursor = new Cursor<>(txnLog, head);
        this.transactionView = new DefaultTransactionView(this.cursor);
        this.nodeView = new DefaultNodeView(this.cursor);

        this.namedReferences = new HashMap<>(builder.getContext().getNamedReferences());
    }

    @Override
    public Node getNamedReference(String name) {
        return this.namedReferences.get(name);
    }

}
