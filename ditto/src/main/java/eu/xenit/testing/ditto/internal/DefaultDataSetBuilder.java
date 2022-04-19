package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.BootstrapConfiguration;
import eu.xenit.testing.ditto.api.BuilderConfigurator;
import eu.xenit.testing.ditto.api.DataSetBuilder;
import eu.xenit.testing.ditto.api.TransactionBuilder;
import eu.xenit.testing.ditto.api.TransactionCustomizer;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.DefaultTransaction.DefaultTransactionBuilder;
import eu.xenit.testing.ditto.internal.repository.DataRepository;
import eu.xenit.testing.ditto.internal.repository.Cursor;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;

public final class DefaultDataSetBuilder implements DataSetBuilder {

    private final DataRepository repository;
    private final Cursor cursor;

    @Getter(AccessLevel.PACKAGE)
    private final RootContext context;

    @Getter(AccessLevel.PACKAGE)
    private final LinkedList<Transaction> transactions = new LinkedList<>();

    DefaultDataSetBuilder(BootstrapConfiguration config) {
        this(new DataRepository(), null, new RootContext(config));
    }

    DefaultDataSetBuilder(DataRepository repository, Cursor cursor, RootContext context) {
        this.repository = repository;
        this.cursor = cursor != null ? cursor : repository.getRootCursor();
        this.context = context;

        // TODO validate cursor belongs to this DataRepository instance ?
    }

    @Override
    public DataSetBuilder addTransaction(Consumer<TransactionBuilder> callback) {
        DefaultTransactionBuilder txnBuilder = DefaultTransaction.builder(this.context, this::projection);
        callback.accept(txnBuilder);
        this.transactions.add(txnBuilder.build());
        return this;
    }

    @Override
    public DataSetBuilder configure(Consumer<BuilderConfigurator> configurator) {
        configurator.accept(new DefaultBuilderConfigurator(this.context));
        return this;
    }

    @Override
    public AlfrescoDataSet build() {
        return this.projection();
    }

    private AlfrescoDataSet projection(Transaction... currentTransactions) {
        // build a projected snapshot:

        // 1. on top of the current cursor
        // 2. adding committed new transactions
        // 3. list of "in-flight" transactions from the argument

        Cursor projectionCursor = this.repository.process(
                this.cursor,
                Stream.concat(this.transactions.stream(), Stream.of(currentTransactions)));

        return new DefaultAlfrescoDataSet(this.repository, projectionCursor, this.context);
    }
}
