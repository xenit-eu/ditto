package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.BootstrapConfiguration;
import eu.xenit.testing.ditto.api.BuilderConfigurer;
import eu.xenit.testing.ditto.api.DataSetBuilder;
import eu.xenit.testing.ditto.api.Transaction;
import eu.xenit.testing.ditto.api.TransactionCustomizer;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionBuilder;
import java.util.LinkedList;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;

public final class DefaultDataSetBuilder implements DataSetBuilder {

    @Getter(AccessLevel.PACKAGE)
    private RootContext context;

    @Getter(AccessLevel.PACKAGE)
    private LinkedList<Transaction> transactions = new LinkedList<>();

    DefaultDataSetBuilder(BootstrapConfiguration config)
    {
        context = new RootContext(config.getBootstrapInstant());
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
    public DataSetBuilder bootstrapAlfresco() {
        return this.skipToTransaction(6)
                .addTransaction(txn -> txn
                        .skipToNodeId(12)
                        .addNode(node -> node
                                .type(System.STORE_ROOT)
                        )
                        .addFolder("Company Home")
                        .addFolder("Space Templates")

                        .skipToNodeId(26)
                        .addFolder("User Homes")
                );
    }

    @Override
    public AlfrescoDataSet build() {
        return new DefaultAlfrescoDataSet(this);
    }
}