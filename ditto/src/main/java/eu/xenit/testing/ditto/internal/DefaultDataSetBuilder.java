package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.BootstrapConfiguration;
import eu.xenit.testing.ditto.api.BuilderConfigurer;
import eu.xenit.testing.ditto.api.DataSetBuilder;
import eu.xenit.testing.ditto.api.TransactionCustomizer;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionBuilder;
import eu.xenit.testing.ditto.internal.repository.DataRepository;
import eu.xenit.testing.ditto.internal.repository.Cursor;
import java.util.LinkedList;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;

public final class DefaultDataSetBuilder implements DataSetBuilder {

    private final DataRepository storage;
    private final Cursor cursor;

    @Getter(AccessLevel.PACKAGE)
    private RootContext context;

    @Getter(AccessLevel.PACKAGE)
    private LinkedList<Transaction> transactions = new LinkedList<>();

    DefaultDataSetBuilder(BootstrapConfiguration config)
    {
        this(new DataRepository(), null, new RootContext(config));
    }

    DefaultDataSetBuilder(DataRepository storage, Cursor cursor, RootContext context) {
        this.storage = storage;
        this.cursor = cursor != null ? cursor : storage.getRootCursor();
        this.context = context;

        // TODO validate cursor belongs to this DataStorage instance ?
    }

    @Override
    public DataSetBuilder addTransaction(Consumer<TransactionCustomizer> callback) {
        TransactionBuilder txnBuilder = DefaultTransaction.builder(this.context);
        callback.accept(txnBuilder);
        this.transactions.add(txnBuilder.build());
        return this;
    }

    @Override
    public DataSetBuilder skipToTransaction(long newTxnSeqId) {
        this.context.skipToTransactionId(newTxnSeqId);
        return this;
    }

    @Override
    public DataSetBuilder configure(Consumer<BuilderConfigurer> configurer) {
        configurer.accept(new DefaultBuilderConfigurer(this.context));
        return this;
    }



    @Override
    public AlfrescoDataSet build() {

        // process all the write-transactions:
//        RecordLog<Transaction> txnLog = new RecordLog<>();
//        RecordLogEntry<Transaction> head = txnLog.process(this.transactions.stream());

        Cursor newCursor = this.storage.process(this.cursor, this.transactions.stream());
//
//        this.cursor = new Cursor<>(txnLog, head);

        return new DefaultAlfrescoDataSet(this.storage, newCursor);
    }
}
