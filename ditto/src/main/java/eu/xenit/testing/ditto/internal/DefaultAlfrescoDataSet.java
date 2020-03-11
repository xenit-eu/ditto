package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.ContentView;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.internal.repository.DataRepository;
import eu.xenit.testing.ditto.internal.repository.Cursor;

public class DefaultAlfrescoDataSet implements AlfrescoDataSet {

    private final DataRepository storage;
    private final Cursor cursor;

    DefaultAlfrescoDataSet(DataRepository storage, Cursor cursor) {
        this.storage = storage;
        this.cursor = cursor;
    }

    @Override
    public Node getNamedReference(String name) {
        return null;
    }

    @Override
    public TransactionView getTransactionView() {
        return new DefaultTransactionView(storage.getTxnRepository(), cursor);
    }

    @Override
    public NodeView getNodeView() {
        return new DefaultNodeView(storage.getNodeRepository(), cursor);
    }

    @Override
    public ContentView getContentView() {
        return new DefaultContentView(storage.getContentRepository(), cursor);
    }
}
