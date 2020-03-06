package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.ContentView;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.record.Cursor;
import eu.xenit.testing.ditto.internal.record.RecordLog;
import eu.xenit.testing.ditto.internal.record.RecordLogEntry;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public class DefaultAlfrescoDataSet implements AlfrescoDataSet {

    @Getter
    private TransactionView transactionView;

    @Getter
    public NodeView nodeView;

    @Getter
    public ContentView contentView;

    private Cursor<Transaction> cursor;

    private final Map<String, Node> namedReferences;

    DefaultAlfrescoDataSet(DefaultDataSetBuilder builder) {

        RecordLog<Transaction> txnLog = new RecordLog<>();
        RecordLogEntry<Transaction> head = txnLog.process(builder.getTransactions().stream());

        this.cursor = new Cursor<>(txnLog, head);
        this.transactionView = new DefaultTransactionView(this.cursor);
        this.nodeView = new DefaultNodeView(this.cursor);
        this.contentView = new DefaultContentView(this.cursor);

        this.namedReferences = new HashMap<>(builder.getContext().getNamedReferences());
    }

    @Override
    public Node getNamedReference(String name) {
        return this.namedReferences.get(name);
    }

}
