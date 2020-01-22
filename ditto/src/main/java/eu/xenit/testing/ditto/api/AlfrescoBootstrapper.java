package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;

class AlfrescoBootstrapper {

    static void configureBootstrap(BootstrapConfiguration config) {
        config.withNamespaces(System.NAMESPACE, Content.NAMESPACE);
    }

    static DataSetBuilder bootstrap(DataSetBuilder builder)
    {
        builder.skipToTransaction(6)
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

        return builder;
    }

}
