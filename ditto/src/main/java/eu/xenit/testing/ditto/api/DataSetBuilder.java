package eu.xenit.testing.ditto.api;

import java.util.function.Consumer;

public interface DataSetBuilder {

    DataSetBuilder addTransaction(Consumer<TransactionBuilder> callback);

    DataSetBuilder configure(Consumer<BuilderConfigurator> callback);

    AlfrescoDataSet build();

    /**
     * @deprecated you can configure the next transaction sequence id with {@link #configure(Consumer)}
     */
    @Deprecated
    default DataSetBuilder skipToTransaction(long newTxnSeqId) {
        return this.configure(config -> config.skipToTxnId(newTxnSeqId));
    }

}
