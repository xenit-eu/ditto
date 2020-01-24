package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.data.ContentModel.User;

class AlfrescoBootstrapper {

    static void configureBootstrap(BootstrapConfiguration config) {
        config.withNamespaces(System.NAMESPACE, Content.NAMESPACE);
    }

    static DataSetBuilder bootstrap(DataSetBuilder builder) {
        builder.addTransaction(txn -> {
            txn.addNode(userStoreRoot -> {
                userStoreRoot.type(System.STORE_ROOT);
                userStoreRoot.storeRefIdentifier("alfrescoUserStore");
                userStoreRoot.storeRefProtocol("user");
            });

            txn.addNode(userStoreContainer -> {
                userStoreContainer.type(System.CONTAINER);
                userStoreContainer.storeRefIdentifier("alfrescoUserStore");
                userStoreContainer.storeRefProtocol("user");
            });

            txn.addNode(people -> {
                people.type(System.CONTAINER);
                people.storeRefIdentifier("alfrescoUserStore");
                people.storeRefProtocol("user");
            });

            txn.addNode(admin -> {
                admin.type(User.USER);
                admin.property(User.USERNAME, "admin");
            });
        });

        builder.skipToTransaction(6)
                .addTransaction(txn -> txn
                        .skipToNodeId(12)
                        .addNode(node -> node
                                .type(System.STORE_ROOT)
                        )
                        .addFolder("Company Home", node -> {
                            node.mlProperty(Content.TITLE, "Company Home");
                            node.mlProperty(Content.DESCRIPTION, "The company root space");
                        })
                        .addFolder("Space Templates")

                        .skipToNodeId(26)
                        .addFolder("User Homes")
                );

        return builder;
    }

}
