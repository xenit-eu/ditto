package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.ContentView;
import eu.xenit.testing.ditto.api.DataSetBuilder;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.internal.repository.DataRepository;
import eu.xenit.testing.ditto.internal.repository.Cursor;

public class DefaultAlfrescoDataSet implements AlfrescoDataSet {

    private final DataRepository repository;
    private final Cursor cursor;
    private final RootContext context;

    DefaultAlfrescoDataSet(DataRepository repository, Cursor cursor, RootContext context) {
        this.repository = repository;
        this.cursor = cursor;
        this.context = context;
    }

    @Override
    public DataSetBuilder toBuilder() {
        return new DefaultDataSetBuilder(this.repository, this.cursor, new RootContext(this.context));
    }

    @Override
    public Node getNamedReference(String name) {
        return null;
    }

    @Override
    public TransactionView getTransactionView() {
        return new DefaultTransactionView(repository.getTxnRepository(), cursor);
    }

    @Override
    public NodeView getNodeView() {
        return new DefaultNodeView(repository.getNodeRepository(), cursor);
    }

    @Override
    public ContentView getContentView() {
        return new DefaultContentView(repository.getContentRepository(), cursor);
    }
}
