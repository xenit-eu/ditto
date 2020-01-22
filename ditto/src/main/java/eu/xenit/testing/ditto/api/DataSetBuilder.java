package eu.xenit.testing.ditto.api;

import java.util.function.Consumer;

public interface DataSetBuilder {

    DataSetBuilder addTransaction(Consumer<TransactionCustomizer> callback);

    DataSetBuilder skipToTransaction(long newTxnSeqId);

    DataSetBuilder configure(Consumer<BuilderConfigurer> configurer);

    DataSetBuilder bootstrapAlfresco();

    AlfrescoDataSet build();

}