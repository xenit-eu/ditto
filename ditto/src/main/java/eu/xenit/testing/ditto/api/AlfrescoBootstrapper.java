package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.data.ContentModel.User;
import eu.xenit.testing.ditto.api.model.Node;
import java.util.function.Consumer;

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
                    Node dataDictionary = txn.addFolder(companyHome, appNode("Data Dictionary", node -> {
                        node.qname(Application.createQName("dictionary"));
                        node.mlProperty(Content.DESCRIPTION, "User managed definitions");
                    }));
                    txn.addFolder(dataDictionary, appNode("Space Templates", node -> {
                        node.mlProperty(Content.DESCRIPTION, "Space folder templates");
                    }));
                    txn.addFolder(dataDictionary, appNode("Presentation Templates", node -> {
                        node.qname(Application.createQName("content_templates"));
                    }));
                    txn.addFolder(dataDictionary, appNode("Email Templates"));


                    txn.skipToNodeId(25);
                    txn.addFolder(companyHome, node -> {
                        node.name("Guest Home");
                    });
                    txn.addNode(companyHome, node -> node.name("User Homes"));
                });

        return builder;
    }

    private Consumer<NodeCustomizer> appNode(String name) {
        return appNode(name, node -> {});
    }

    private Consumer<NodeCustomizer> appNode(String name, Consumer<NodeCustomizer> callback) {
        return node -> {

            node.name(name);
            node.qname(Application.createQName(name.toLowerCase().replace(" ","_")));

            node.aspect(Content.TITLED);
            node.mlProperty(Content.TITLE, name);
            node.mlProperty(Content.DESCRIPTION, capitalize(name));

            node.aspect(Application.UIFACETS);
            node.property(Application.ICON, "space-icon-default");

            callback.accept(node);
        };
    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

}
