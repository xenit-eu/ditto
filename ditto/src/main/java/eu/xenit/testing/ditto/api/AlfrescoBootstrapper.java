package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.Site;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.data.ContentModel.User;
import eu.xenit.testing.ditto.api.data.ContentModel.Version2;
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
                node.storeRefIdentifier("alfrescoUserStore");
                node.storeRefProtocol("user");
                node.property(User.USERNAME, "admin");
            });
        });

        NodeHolder version2StoreRootHolder = new NodeHolder();
        builder.skipToTransaction(4)
                .addTransaction(txn -> {
                    txn.skipToNodeId(10);
                    Node version2StoreRoot = txn.addRoot(root -> {
                        root.storeRefIdentifier("version2Store");
                        root.storeRefProtocol("workspace");
                    });
                    version2StoreRootHolder.set(version2StoreRoot);
                });

        NodeHolder archiveStoreRootHolder = new NodeHolder();
        builder.skipToTransaction(5)
                .addTransaction(txn -> {
                    Node archiveStoreRoot = txn.addRoot(root -> {
                        root.storeRefIdentifier("SpacesStore");
                        root.storeRefProtocol("archive");
                    });
                    archiveStoreRootHolder.set(archiveStoreRoot);
                });

        NodeHolder companyHomeHolder = new NodeHolder();
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
                    companyHomeHolder.set(companyHome);

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

        builder.skipToTransaction(10)
                .addTransaction(txn -> {
                    txn.skipToNodeId(473);
                    Node sitesNode = txn.addNode(companyHomeHolder.get(), sitesNode("Sites",
                            node -> node.mlProperty(Content.DESCRIPTION, "Site Collaboration Spaces")));
                    txn.skipToNodeId(601);
                    Node swsdpNode = txn.addNode(sitesNode, node ->
                            node
                                    .uuid("b4cff62a-664d-4d45-9302-98723eac1319") // node has fixed uuid
                                    .type(Site.SITE)
                                    .name("swsdp")
                                    .aspect(Content.TITLED)
                                    .mlProperty(Content.TITLE, "Sample: Web Site Design Project")
                                    .mlProperty(Content.DESCRIPTION, "This is a Sample Alfresco Team site.")
                                    .property(Site.SITE_VISIBILITY, "PUBLIC")
                                    .property(Site.SITE_PRESET, "site-dashboard"));
                    Node swsdpDocumentLibraryFolder = txn.addFolder(swsdpNode, node ->
                            node.uuid("8f2105b4-daaf-4874-9e8a-2152569d109b") // node has fixed uuid
                                    .name("documentLibrary")
                                    .aspect(Content.TITLED)
                                    .mlProperty(Content.DESCRIPTION, "Document Library"));
                    Node agencyFolder = txn.addFolder(swsdpDocumentLibraryFolder, node ->
                            node.uuid("8bb36efb-c26d-4d2b-9199-ab6922f53c28") // node has fixed uuid
                                    .name("Agency Files")
                                    .aspect(Content.TITLED)
                                    .mlProperty(Content.TITLE, "Agency related files")
                                    .mlProperty(Content.DESCRIPTION,
                                            "This folder holds the agency related files for the project"));
                    Node contractsFolder = txn.addFolder(agencyFolder, node ->
                            node.uuid("e0856836-ed5e-4eee-b8e5-bd7e8fb9384c") // node has fixed uuid
                                    .name("Contracts")
                                    .aspect(Content.TITLED)
                                    .mlProperty(Content.TITLE, "Project contracts")
                                    .mlProperty(Content.DESCRIPTION, "This folder holds the agency contracts"));
                    Node projectContractNode = txn.addDocument(contractsFolder, node ->
                            node.uuid("1a0b110f-1e09-4ca2-b367-fe25e4964a4e")  // node has fixed uuid
                                    .name("Project Contract.pdf")
                                    .aspect(Content.TITLED)
                                    .mlProperty(Content.TITLE, "Project Contract for Green Energy")
                                    .mlProperty(Content.DESCRIPTION, "Contract for the Green Energy project")
                                    .aspect(Content.VERSIONABLE)
                                    .property(Content.VERSION_LABEL, "1.1")
                                    .property(Content.AUTO_VERSION, true)
                                    .property(Content.INITIAL_VERSION, true)
                                    .property(Content.AUTO_VERSION_ON_UPDATE_PROPS, false));
                    Node projectContractVersionHistory = txn
                            .addNode(version2StoreRootHolder.get(), Version2.VERSION_HISTORY,
                                    versionHistoryNode(projectContractNode, NO_OP));
                    txn.addNode(projectContractVersionHistory, Version2.VERSION,
                            versionNode(projectContractNode, NO_OP));
                });

        builder.configure(config -> {
            config.setDefaultParentNode(companyHomeHolder.get());
        });

        return builder;
    }

    private Consumer<NodeCustomizer> appNode(String name) {
        return appNode(name, node -> {
        });
    }

    private Consumer<NodeCustomizer> appNode(String name, Consumer<NodeCustomizer> callback) {
        return node -> {

            node.name(name);
            node.qname(Application.createQName(name.toLowerCase().replace(" ", "_")));

            node.aspect(Content.TITLED);
            node.mlProperty(Content.TITLE, name);
            node.mlProperty(Content.DESCRIPTION, capitalize(name));

            node.aspect(Application.UIFACETS);
            node.property(Application.ICON, "space-icon-default");

            callback.accept(node);
        };
    }

    private Consumer<NodeCustomizer> sitesNode(String name, Consumer<NodeCustomizer> callback) {
        return node -> {

            node.name(name);
            node.qname(Site.createQName(name.toLowerCase().replace(" ", "_")));

            node.aspect(Content.TITLED);
            node.mlProperty(Content.TITLE, name);

            node.aspect(Application.UIFACETS);
            node.property(Application.ICON, "space-icon-default");

            callback.accept(node);
        };
    }

    private Consumer<NodeCustomizer> versionHistoryNode(Node liveNode, Consumer<NodeCustomizer> callback) {
        return node -> {
            node.type(Version2.VERSION_HISTORY)
                    .storeRefProtocol("workspace")
                    .storeRefIdentifier("version2Store")
                    .property(Version2.VERSIONED_NODE_ID, liveNode.getNodeRef().getUuid());
            callback.accept(node);
        };
    }

    private Consumer<NodeCustomizer> versionNode(Node liveNode, Consumer<NodeCustomizer> callback) {
        return node -> {
            node
                    .storeRefProtocol("workspace")
                    .storeRefIdentifier("version2Store");
            node.type(liveNode.getType());

            liveNode.getProperties().stream().forEach((entry) -> {
                if (Content.CREATOR.equals(entry.getKey())) {
                    node.property(Version2.FROZEN_CREATOR, entry.getValue());
                    return;
                }
                if (Content.CREATED.equals(entry.getKey())) {
                    node.property(Version2.FROZEN_CREATED, entry.getValue());
                    return;
                }
                if (Content.MODIFIER.equals(entry.getKey())) {
                    node.property(Version2.FROZEN_MODIFIER, entry.getValue());
                    return;
                }
                if (Content.MODIFIED.equals(entry.getKey())) {
                    node.property(Version2.FROZEN_MODIFIED, entry.getValue());
                    return;
                }
                if (Content.VERSION_LABEL.equals(entry.getKey())) {
                    node.property(Version2.VERSION_LABEL, entry.getValue());
                }
                node.property(entry.getKey(), entry.getValue());
            });

            liveNode.getAspects().forEach(node::aspect);

            node.property(Version2.FROZEN_NODE_REF, liveNode.getNodeRef().toString());
            node.property(Version2.VERSION_DESCRIPTION, null);
            node.property(Content.VERSION_LABEL, null);

            callback.accept(node);
        };
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private final static Consumer<NodeCustomizer> NO_OP = whatever -> {
    };

}
