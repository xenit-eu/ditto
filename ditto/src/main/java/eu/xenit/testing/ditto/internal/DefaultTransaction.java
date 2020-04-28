package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeCustomizer;
import eu.xenit.testing.ditto.api.TransactionBuilder;
import eu.xenit.testing.ditto.api.content.SwarmContentServiceCustomizer;
import eu.xenit.testing.ditto.api.data.ContentModel;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.DefaultNode.NodeBuilder;
import eu.xenit.testing.ditto.internal.content.ContentUrlProviderSpi;
import eu.xenit.testing.ditto.internal.content.FileSystemContentUrlProvider;
import eu.xenit.testing.ditto.internal.content.SwarmContentServiceConfiguration;
import eu.xenit.testing.ditto.internal.content.SwarmContentUrlProvider;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DefaultTransaction implements Transaction {

    private final long id;
    private final long version = 1;
    private final long serverId = 1;
    private final String changeId;
    private final long commitTimeMs;

    private Set<Node> updated;
    private Set<Node> deleted;

    private DefaultTransaction(DefaultTransactionBuilder builder) {
        this.id = builder.context.txnId;
        this.changeId = builder.changeId;
        this.commitTimeMs = builder.commitTimeMs;

        this.updated = new LinkedHashSet<>(builder.updated.values());
        this.deleted = new LinkedHashSet<>(builder.deleted.values());

    }

    public String toString() {
        return String.format("Txn[id=%s; uuid=%s]", this.id, this.changeId);
    }

    static DefaultTransactionBuilder builder(RootContext context, Function<Transaction, AlfrescoDataSet> projection) {
        return new DefaultTransactionBuilder(context, projection);
    }

    static class TransactionContext {

        private final RootContext rootContext;

        @Getter
        private final long txnId;

        TransactionContext(RootContext context) {
            this.rootContext = context;
            this.txnId = context.nextTxnId();
        }

        public Instant now() {
            return this.rootContext.now();
        }

        public Locale defaultLocale() {
            return this.rootContext.defaultLocale();
        }

        public void skipToNodeId(long nodeId) {
            this.rootContext.skipToNodeId(nodeId);
        }

        public long nextNodeId() {
            return this.rootContext.nextNodeId();
        }

        public void skipToContentDataId(long contentDataId) {
            this.rootContext.skipToContentDataId(contentDataId);
        }

        public long nextContentDataId() {
            return this.rootContext.nextContentDataId();
        }

        @Setter
        private ContentUrlProviderSpi contentUrlProvider = null;

        public ContentUrlProviderSpi getContentUrlProvider() {
            return this.contentUrlProvider != null
                    ? this.contentUrlProvider
                    : this.rootContext.getContentUrlProvider();
        }

        public void createNamedReference(String name, Node node) {
            this.rootContext.createNamedReference(name, node);
        }

        public QName resolveQName(String qname) {
            return this.rootContext.resolveQName(qname);
        }

        QName getDefaultChildAssocType() {
            return this.rootContext.getDefaultChildAssocType();
        }

        Node getDefaultParentNode() {
            return this.rootContext.getDefaultParentNode();
        }

        public Node getNodeByNodeRef(NodeReference nodeRef) {
            return this.rootContext.getNodeByNodeRef(nodeRef);
        }

        void onNodeSaved(Node node) {
            this.rootContext.onNodeSaved(node);
        }
    }

    public static class DefaultTransactionBuilder implements TransactionBuilder {

        private String changeId;

        private HashMap<NodeReference, Node> updated = new LinkedHashMap<>();
        private HashMap<NodeReference, Node> deleted = new LinkedHashMap<>();

        private long commitTimeMs;

        private final Function<Transaction, AlfrescoDataSet> projection;

        private final TransactionContext context;

        public DefaultTransactionBuilder(RootContext rootContext, Function<Transaction, AlfrescoDataSet> projection) {
            this.context = new TransactionContext(rootContext);
            this.projection = projection;

            this.commitTimeMs = rootContext.commitTimeInMillis();
            this.changeId = UUID.randomUUID().toString();
        }


        Transaction build() {
            return new DefaultTransaction(this);
        }

        @Override
        public TransactionBuilder skipToNodeId(long nodeId) {
            this.context.skipToNodeId(nodeId);
            return this;
        }

        @Override
        public Node addNode(Node parent, QName assocType, Consumer<NodeCustomizer> customizer) {
            if (parent == null) {
                parent = this.context.getDefaultParentNode();
            }

            if (assocType == null) {
                assocType = this.context.getDefaultChildAssocType();
            }

            NodeBuilder builder = DefaultNode.builder(this.context, parent, assocType);
            customizer.accept(builder);

            DefaultNode node = builder.build();
            this.updated.put(node.getNodeRef(), node);
            return node;
        }

        @Override
        public Node addNode(Node parent, Consumer<NodeCustomizer> customizer) {
            return this.addNode(parent, this.context.getDefaultChildAssocType(), customizer);
        }

        @Override
        public Node addNode(Consumer<NodeCustomizer> customizer) {
            return this.addNode(null, null, customizer);
        }

        @Override
        public Node addRoot(Consumer<NodeCustomizer> callback) {
            NodeBuilder builder = DefaultNode.builder(this.context, null, null);
            builder.type(System.STORE_ROOT);
            builder.aspect(System.ASPECT_ROOT);
            callback.accept(builder);

            DefaultNode node = builder.build();
            this.updated.put(node.getNodeRef(), node);
            return node;
        }

        @Override
        public Node addDocument(Node parent, Consumer<NodeCustomizer> callback) {
            return this.addNode(parent, node -> {
                node.type(Content.CONTENT);
                callback.accept(node);
            });
        }

        @Override
        public Node addFolder(Node parent, Consumer<NodeCustomizer> callback) {
            return this.addNode(parent, node -> {
                node.type(ContentModel.Content.FOLDER);
                callback.accept(node);
            });
        }

        @Override
        public Node getNodeByNodeRef(String nodeRef) {
            return this.context.getNodeByNodeRef(NodeReference.parse(nodeRef));
        }

        @Override
        public DefaultTransactionBuilder resetDefaultContentUrlProvider() {
            this.context.setContentUrlProvider(null);
            return this;
        }

        @Override
        public DefaultTransactionBuilder useFileSystemContentService() {
            this.context.setContentUrlProvider(new FileSystemContentUrlProvider());
            return this;
        }

        @Override
        public DefaultTransactionBuilder useSwarmContentService(Consumer<SwarmContentServiceCustomizer> customizer) {
            SwarmContentServiceConfiguration config = new SwarmContentServiceConfiguration();
            customizer.accept(config);

            SwarmContentUrlProvider swarm = new SwarmContentUrlProvider(config.bucket());
            this.context.setContentUrlProvider(swarm);

            return this;
        }

        @Override
        public Optional<Node> getNode(long nodeId) {
            return this.projection.apply(this.build()).getNodeView().getNode(nodeId);
        }

        @Override
        public Optional<Node> getNode(NodeReference nodeRef) {
            return this.projection.apply(this.build()).getNodeView().getNode(nodeRef);
        }

        @Override
        public Stream<Node> allNodes() {
            return this.projection.apply(this.build()).getNodeView().allNodes();
        }

        @Override
        public Stream<Node> rootNodes() {
            return this.projection.apply(this.build()).getNodeView().rootNodes();
        }

        @Override
        public Optional<Node> getCompanyHome() {
            return this.projection.apply(this.build()).getNodeView().getCompanyHome();
        }
    }
}
