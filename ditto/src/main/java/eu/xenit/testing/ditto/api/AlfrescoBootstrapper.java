package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.data.ContentModel;
import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.data.ContentModel.User;
import eu.xenit.testing.ditto.api.model.Node;

class AlfrescoBootstrapper<T extends DataSetBuilder> {

    void configureBootstrap(BootstrapConfiguration config) {
        config.withNamespaces(System.NAMESPACE, Content.NAMESPACE);
    }

    T bootstrap(T builder) {
        builder.addTransaction(txn -> {
            Node userStoreRoot = txn.addRoot(root -> {
                root.storeRefIdentifier("alfrescoUserStore");
                root.storeRefProtocol("user");
            });

            Node userStoreContainer = txn.addNode(userStoreRoot, System.CHILDREN, node -> {
                node.type(System.CONTAINER);
                node.storeRefIdentifier("alfrescoUserStore");
                node.storeRefProtocol("user");
            });

            Node people = txn.addNode(userStoreContainer, System.CHILDREN, node -> {
                node.type(System.CONTAINER);
                node.storeRefIdentifier("alfrescoUserStore");
                node.storeRefProtocol("user");
            });

            txn.addNode(people, System.CHILDREN, node -> {
                node.type(User.USER);
                node.property(User.USERNAME, "admin");
            });
        });

        builder.skipToTransaction(6)
                .addTransaction(txn -> {
                    txn.skipToNodeId(12);
                    Node root = txn.addRoot(node -> {
                    });
                    Node companyHome = txn.addNode(root, System.CHILDREN, node -> {
                        node.type(Content.FOLDER);
                        node.name("Company Home");
                        node.qname(Application.createQName("company_home"));

                        node.aspect(Content.TITLED);
                        node.mlProperty(Content.TITLE, "Company Home");
                        node.mlProperty(Content.DESCRIPTION, "The company root space");

                        node.aspect(Application.UIFACETS);
                        node.property(Application.ICON, "space-icon-default");
                    });
                    Node dataDictionary = txn.addNode(companyHome, Content.CONTAINS, node -> {
                        node.name("Data Dictionary");
                    });
                    txn.addNode(dataDictionary, node -> {
                        node.name("Space Templates");
                    });

                    txn.skipToNodeId(26);
                    txn.addNode(companyHome, node -> node.name("User Homes"));
                });

        return builder;
    }

}
