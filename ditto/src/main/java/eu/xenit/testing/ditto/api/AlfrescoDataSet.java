package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.model.Node;
import java.time.Instant;
import java.util.function.Consumer;

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
        BootstrapConfiguration config = BootstrapConfiguration
                .withBootstrapInstant(bootstrapInstant);

        return factory.createBuilder(config);
    }

    static DataSetBuilder builder() {
        return builder(Instant.now());
    }

    static DataSetBuilder bootstrapAlfresco() {
        return bootstrapAlfresco(Instant.now());
    }

    static DataSetBuilder bootstrapAlfresco(Instant bootstrapInstant) {
        return bootstrapAlfresco(bootstrapInstant, (config) -> {});
    }

    static DataSetBuilder bootstrapAlfresco(Instant bootstrapInstant, Consumer<BootstrapConfiguration> callback) {
        DataSetBuilderFactory factory = DataSetBuilderProvider.getInstance().getFactory();
        BootstrapConfiguration config = BootstrapConfiguration.withBootstrapInstant(bootstrapInstant);

        AlfrescoBootstrapper<DataSetBuilder> alfrescoBootstrapper = new AlfrescoBootstrapper<>();
        alfrescoBootstrapper.configureBootstrap(config);
        callback.accept(config);

        DataSetBuilder builder = factory.createBuilder(config);
        return alfrescoBootstrapper.bootstrap(builder);
    }

    static AlfrescoDataSet empty() {
        return builder().build();
    }

    Node getNamedReference(String name);

    TransactionView getTransactionView();
    NodeView getNodeView();
    ContentView getContentView();



}
