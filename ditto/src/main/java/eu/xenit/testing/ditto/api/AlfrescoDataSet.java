package eu.xenit.testing.ditto.api;

import java.time.Instant;

public interface AlfrescoDataSet {

    /**
     * Allows to specify the instant the transaction log gets bootstrapped,
     * making all tests independent from the current system clock
     *
     * @param bootstrapInstant is the point in time the Alfresco data set is bootstrapped
     * @return a builder-object to populate the dataset
     */
    static DataSetBuilder builder(Instant bootstrapInstant) {
        DataSetBuilderFactory factory = DataSetBuilderProvider.getInstance().getFactory();
        BootstrapConfiguration config = BootstrapConfiguration.withBootstrapInstant(bootstrapInstant);

        return factory.createBuilder(config);
    }

    static DataSetBuilder builder() {
        return builder(Instant.now());
    }

    static AlfrescoDataSet empty() {
        return builder().build();
    }

    Node getNamedReference(String name);

    TransactionView getTransactionView();
    NodeView getNodeView();



}
