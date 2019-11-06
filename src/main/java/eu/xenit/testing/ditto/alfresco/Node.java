package eu.xenit.testing.ditto.alfresco;

import static eu.xenit.testing.ditto.alfresco.NodeReference.STOREREF_ID_SPACESSTORE;
import static eu.xenit.testing.ditto.alfresco.NodeReference.STOREREF_PROT_WORKSPACE;

import eu.xenit.testing.ditto.alfresco.DictionaryModel.ContentModel;
import eu.xenit.testing.ditto.alfresco.Transaction.TransactionContext;
import eu.xenit.testing.ditto.util.MimeTypes;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

public class Node {

    @Getter
    private final long nodeId;

    @Getter
    @NonNull
    private final NodeReference nodeRef;

    @Getter
    @NonNull
    private final String type;

    @Getter
    private final NodeProperties properties;

    @Getter
    private final Set<String> aspects;

    @Getter(AccessLevel.PACKAGE)
    private boolean isDocument;

    // move state to node-context and encode this in cm:content ?
    private String mimetype;

    // move state to node-context and encode this in cm:content ?
    @Getter
    private long size;

    // TODO we should hide this, it only contains something if the
    // content is specified and not auto-generated
    @Getter
    private final byte[] content;

    private static NodeInitializer init = new NodeInitializer();

    private Node(NodeBuilder builder) {
        this.nodeId = builder.nodeId;
        this.nodeRef = builder.getNodeRef();
        this.type = builder.type;
        this.properties = new NodeProperties(builder.properties);
        this.aspects = new HashSet<>(builder.aspects);

        this.isDocument = builder.isDocument;
        this.mimetype = builder.mimetype;
        this.size = builder.size;

        this.content = builder.content;

        init.accept(this, builder.context);
    }

    public static NodeBuilder builder(TransactionContext context) {
        return new NodeBuilder(context);
    }

    public String getMimeType() {
        // should we get this from the content-property ?
        return this.mimetype != null
                ? this.mimetype
                : MimeTypes.APPLICATION_OCTET_STREAM;

    }

    public String getName() {
        return (String) this.getProperties().get(ContentModel.NAME);
    }

    public static class NodeContext {

        private final TransactionContext txnContext;

        @Getter
        private final Instant instant;

        private NodeContext (TransactionContext txnContext)
        {
            this.txnContext = txnContext;
            this.instant = txnContext.now();
        }

        @Setter
        private ContentUrlProvider contentUrlProvider = null;

        public ContentUrlProvider getContentUrlProvider() {
            return this.contentUrlProvider != null
                    ? this.contentUrlProvider
                    : this.txnContext.getContentUrlProvider();
        }

        public void createNamedReference(String name, Node node) {
            this.txnContext.createNamedReference(name, node);
        }
    }

    public static class NodeBuilder {


        private final NodeContext context;

        private NodeBuilder(TransactionContext context) {
            this.nodeId = context.nextNodeId();
            this.context = new NodeContext(context);
        }

        public Node build() {
            Node node = new Node(this);

            this.callbacks.forEach(callback -> callback.accept(node));

            return node;
        }

        @Getter
        private final long nodeId;

        @Getter
        @Setter
        @Accessors(fluent = true, chain = true)
        private String storeRefProtocol = STOREREF_PROT_WORKSPACE;

        @Getter
        @Setter
        @Accessors(fluent = true, chain = true)
        private String storeRefIdentifier = STOREREF_ID_SPACESSTORE;

        @Getter
        @Setter
        @Accessors(fluent = true, chain = true)
        private String uuid = UUID.randomUUID().toString();


        @Getter
        @Setter
        @Accessors(fluent = true, chain = true)
        private boolean isDocument = false;

        @Getter
        @Accessors(fluent = true)
        private String mimetype = MimeTypes.APPLICATION_OCTET_STREAM;

        public NodeBuilder mimetype(String mimetype)
        {
            this.mimetype = mimetype;
            this.isDocument = true;
            return this;
        }

        @Getter
        @Accessors(fluent = true)
        private long size = -1;

        public NodeBuilder size(long size)
        {
            if (size < 0) {
                throw new IllegalArgumentException("Argument 'size' should be > 0");
            }
            this.size = size;
            this.isDocument = true;
            return this;
        }

        public NodeReference getNodeRef() {
            return new NodeReference(this.storeRefProtocol, this.storeRefIdentifier, this.uuid);
        }

        @Getter
        @Setter
        @Accessors(fluent = true, chain = true)
        private String type = ContentModel.CONTENT;

        @Getter
        private Map<String, Serializable> properties = new HashMap<>();

        @Getter
        @Accessors(fluent = true)
        private byte[] content;

        @Getter
        @Setter
        @Accessors(fluent = true, chain = true)
        private Charset charset;

        public NodeBuilder properties(Map<String, Serializable> properties) {
            Objects.requireNonNull(properties, "Argument 'properties' should not be null");
            this.properties.putAll(properties);
            return this;
        }

        public NodeBuilder property(String key, String value) {
            Objects.requireNonNull(key, "Argument 'key' should not be null");
            this.properties.put(key, value);
            return this;
        }

        @Getter
        private Set<String> aspects = new LinkedHashSet<>();


        public NodeBuilder aspects(Set<String> aspects)
        {
            Objects.requireNonNull(aspects, "Argument 'aspects' is required");
            this.aspects.forEach(this::aspect);
            return this;
        }

        public NodeBuilder aspect(String aspect)
        {
            Objects.requireNonNull(aspect, "Argument 'aspect' is required");
            this.aspects.add(aspect);
            return this;
        }

        public NodeBuilder name(String name)
        {
            Objects.requireNonNull(name, "Argument 'name' is required");
            this.property(ContentModel.NAME, name);
            return this;
        }


        public NodeBuilder content(String content) {
            return this.content(content, StandardCharsets.UTF_8);
        }

        public NodeBuilder content(String content, Charset charset) {
            this.charset = charset;
            return this.content(content.getBytes(charset));
        }

        public NodeBuilder content(byte[] content) {
            this.content = content;
            return this;
        }

        private List<Consumer<Node>> callbacks = new ArrayList<>();
        public NodeBuilder callback(Consumer<Node> callback) {
            this.callbacks.add(callback);
            return this;
        }

        public NodeBuilder createNamedReference(String name) {
            return this.callback(node -> this.context.createNamedReference(name, node));
        }

        public NodeBuilder createNamedReference() {
            return this.callback(node -> this.context.createNamedReference(node.getName(), node));
        }
    }


}
