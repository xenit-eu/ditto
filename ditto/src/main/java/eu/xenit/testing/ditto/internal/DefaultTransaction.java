package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.NodeCustomizer;
import eu.xenit.testing.ditto.api.content.SwarmContentServiceCustomizer;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.content.ContentUrlProviderSpi;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.api.TransactionCustomizer;
import eu.xenit.testing.ditto.api.data.ContentModel;
import eu.xenit.testing.ditto.internal.DefaultNode.NodeBuilder;
import eu.xenit.testing.ditto.internal.content.FileSystemContentUrlProvider;
import eu.xenit.testing.ditto.internal.content.SwarmContentServiceConfiguration;
import eu.xenit.testing.ditto.internal.content.SwarmContentUrlProvider;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
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

    private DefaultTransaction(TransactionBuilder builder)
    {
        this.id = builder.txnSeqId;
        this.changeId = builder.changeId;
        this.commitTimeMs = builder.commitTimeMs;

        this.updated = new LinkedHashSet<>(builder.updated.values());
        this.deleted = new LinkedHashSet<>(builder.deleted.values());

    }

    public String toString() {
        return String.format("Txn[id=%s; uuid=%s]", this.id, this.changeId);
    }

    static TransactionBuilder builder(RootContext context) {
        return new TransactionBuilder(context);
    }

    static class TransactionContext {

        private final RootContext rootContext;

        TransactionContext(RootContext context) {
            this.rootContext = context;
        }

        public Instant now() {
            return this.rootContext.now();
        }

        public void skipToNodeId(long nodeId) {
            this.rootContext.skipToNodeId(nodeId);
        }

        public long nextNodeId() {
            return this.rootContext.nextNodeId();
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
    }

    public static class TransactionBuilder implements TransactionCustomizer {

        private String changeId;

        private HashMap<NodeReference, Node> updated = new LinkedHashMap<>();
        private HashMap<NodeReference, Node> deleted = new LinkedHashMap<>();

        private long txnSeqId;
        private long commitTimeMs;

        private final TransactionContext context;

        private TransactionBuilder(RootContext rootContext) {
            this.context = new TransactionContext(rootContext);

            this.txnSeqId = rootContext.nextTxnId();
            this.commitTimeMs = rootContext.commitTimeInMillis();
            this.changeId = UUID.randomUUID().toString();
        }

        DefaultTransaction build() {
            return new DefaultTransaction(this);
        }

        @Override
        public TransactionBuilder skipToNodeId(long nodeId)
        {
            this.context.skipToNodeId(nodeId);
            return this;
        }

        @Override
        public TransactionBuilder addNode(Consumer<NodeCustomizer> customizer) {
            NodeBuilder builder = DefaultNode.builder(this.context);
            customizer.accept(builder);

            DefaultNode node = builder.build();
            this.updated.put(node.getNodeRef(), node);
            return this;
        }

        @Override
        public TransactionBuilder addNode() {
            this.addNode(node -> { });
            return this;
        }

        @Override
        public TransactionBuilder addDocument(String name)
        {
            return this.addDocument(name, (node) -> { });
        }

        @Override
        public TransactionBuilder addDocument(String name, Consumer<NodeCustomizer> callback) {
            return this.addNode(nodeBuilder -> {
                nodeBuilder.isDocument(true);
                nodeBuilder.type(ContentModel.Content.CONTENT);
                nodeBuilder.name(name);
                callback.accept(nodeBuilder);
            });
        }

        @Override
        public TransactionBuilder addFolder(String name) {
            return this.addFolder(name, (node) -> { });
        }

        @Override
        public TransactionBuilder addFolder(String name, Consumer<NodeCustomizer> callback) {
            return this.addNode(nodeBuilder -> {
                nodeBuilder.type(ContentModel.Content.FOLDER);
                nodeBuilder.name(name);
                callback.accept(nodeBuilder);
            });
        }

        @Override
        public TransactionBuilder resetDefaultContentUrlProvider() {
            this.context.setContentUrlProvider(null);
            return this;
        }

        @Override
        public TransactionBuilder useFileSystemContentService() {
            this.context.setContentUrlProvider(new FileSystemContentUrlProvider());
            return this;
        }

        @Override
        public TransactionBuilder useSwarmContentService(Consumer<SwarmContentServiceCustomizer> customizer) {
            SwarmContentServiceConfiguration config = new SwarmContentServiceConfiguration();
            customizer.accept(config);

            SwarmContentUrlProvider swarm = new SwarmContentUrlProvider(config.bucket());
            this.context.setContentUrlProvider(swarm);

            return this;
        }

    }
}
