package eu.xenit.testing.ditto.alfresco;

import eu.xenit.testing.ditto.alfresco.DictionaryModel.ContentModel;
import eu.xenit.testing.ditto.alfresco.Node.NodeBuilder;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

@Getter
class Transaction {

    private final long id;
    private final long version = 1;
    private final long serverId = 1;
    private final String changeId;
    private final long commitTimeMs;

    private Set<Node> updated;
    private Set<Node> deleted;

    private Transaction(TransactionBuilder builder)
    {
        this.id = builder.txnSeqId;
        this.changeId = builder.changeId;
        this.commitTimeMs = builder.commitTimeMs;

        this.updated = new HashSet<>(builder.updated.values());
        this.deleted = new HashSet<>(builder.deleted.values());

    }

    static TransactionBuilder builder(DefaultContext context) {
        return new TransactionBuilder(context);
    }

    static class TransactionContext {

        private final DefaultContext defaultContext;

        TransactionContext(DefaultContext context) {
            this.defaultContext = context;
        }

        public Instant now() {
            return this.defaultContext.now();
        }

        public void skipToNodeId(long nodeId) {
            this.defaultContext.skipToNodeId(nodeId);
        }

        public long nextNodeId() {
            return this.defaultContext.nextNodeId();
        }

        @Setter
        private ContentUrlProvider contentUrlProvider = null;

        public ContentUrlProvider getContentUrlProvider() {
            return this.contentUrlProvider != null
                    ? this.contentUrlProvider
                    : this.defaultContext.getContentUrlProvider();
        }

        public void createNamedReference(String name, Node node) {
            this.defaultContext.createNamedReference(name, node);
        }
    }

    public static class TransactionBuilder {

        private String changeId;

        private HashMap<NodeReference, Node> updated = new LinkedHashMap<>();
        private HashMap<NodeReference, Node> deleted = new LinkedHashMap<>();

        private long txnSeqId;
        private long commitTimeMs;

        private final TransactionContext context;

        private TransactionBuilder(DefaultContext defaultContext) {
            this.context = new TransactionContext(defaultContext);

            this.txnSeqId = defaultContext.nextTxnId();
            this.commitTimeMs = defaultContext.commitTimeInMillis();
            this.changeId = UUID.randomUUID().toString();
        }

        public Transaction build() {
            return new Transaction(this);
        }

        public TransactionBuilder skipToNodeId(long nodeId)
        {
            this.context.skipToNodeId(nodeId);
            return this;
        }

        public TransactionBuilder addNode(Consumer<NodeBuilder> customizer) {
            NodeBuilder builder = Node.builder(this.context);
            customizer.accept(builder);

            Node node = builder.build();
            this.updated.put(node.getNodeRef(), node);
            return this;
        }

        public TransactionBuilder addNode() {
            this.addNode(node -> { });
            return this;
        }

        public TransactionBuilder addDocument(String name)
        {
            return this.addDocument(name, (NodeBuilder) -> { });
        }

        public TransactionBuilder addDocument(String name, Consumer<NodeBuilder> callback) {
            return this.addNode(nodeBuilder -> {
                nodeBuilder.isDocument(true);
                nodeBuilder.type(ContentModel.CONTENT);
                nodeBuilder.name(name);
                callback.accept(nodeBuilder);
            });
        }

        public TransactionBuilder addFolder(String name) {
            return this.addFolder(name, (NodeBuilder) -> { });
        }

        public TransactionBuilder addFolder(String name, Consumer<NodeBuilder> callback) {
            return this.addNode(nodeBuilder -> {
                nodeBuilder.type(ContentModel.FOLDER);
                nodeBuilder.name(name);
                callback.accept(nodeBuilder);
            });
        }

        public TransactionBuilder setContentUrlProvider(ContentUrlProvider contentUrlProvider)
        {
            this.context.setContentUrlProvider(contentUrlProvider);
            return this;
        }

    }
}
