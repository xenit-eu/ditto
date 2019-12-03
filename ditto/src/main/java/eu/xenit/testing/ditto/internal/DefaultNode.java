package eu.xenit.testing.ditto.internal;

import static eu.xenit.testing.ditto.api.NodeReference.STOREREF_ID_SPACESSTORE;
import static eu.xenit.testing.ditto.api.NodeReference.STOREREF_PROT_WORKSPACE;

import eu.xenit.testing.ditto.api.Node;
import eu.xenit.testing.ditto.api.NodeCustomizer;
import eu.xenit.testing.ditto.api.NodeReference;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.internal.content.ContentContext;
import eu.xenit.testing.ditto.internal.content.ContentUrlProviderSpi;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionContext;
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

public class DefaultNode implements Node {

    @Getter
    private final long nodeId;

    @Getter
    @NonNull
    private final NodeReference nodeRef;

    @Getter
    @NonNull
    private final String type;

    @Getter
    private final DefaultNodeProperties properties;

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

    private DefaultNode(NodeBuilder builder) {
        this.nodeId = builder.nodeId;
        this.nodeRef = builder.getNodeRef();
        this.type = builder.type;
        this.properties = new DefaultNodeProperties(builder.properties);
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

    @Override
    public String getMimeType() {
        // should we get this from the content-property ?
        return this.mimetype != null
                ? this.mimetype
                : MimeTypes.APPLICATION_OCTET_STREAM;

    }

    @Override
    public String getName() {
        return (String) this.getProperties().get(Content.NAME);
    }

    public static class NodeContext implements ContentContext {

        private final TransactionContext txnContext;

        @Getter
        private final Instant instant;

        private NodeContext (TransactionContext txnContext)
        {
            this.txnContext = txnContext;
            this.instant = txnContext.now();
        }

        @Setter
        private ContentUrlProviderSpi contentUrlProvider = null;

        public ContentUrlProviderSpi getContentUrlProvider() {
            return this.contentUrlProvider != null
                    ? this.contentUrlProvider
                    : this.txnContext.getContentUrlProvider();
        }

        public void createNamedReference(String name, Node node) {
            this.txnContext.createNamedReference(name, node);
        }
    }

    public static class NodeBuilder implements NodeCustomizer {


        private final NodeContext context;

        private NodeBuilder(TransactionContext context) {
            this.nodeId = context.nextNodeId();
            this.context = new NodeContext(context);
        }

        @Override
        public DefaultNode build() {
            DefaultNode node = new DefaultNode(this);

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

        @Override
        public NodeBuilder mimetype(String mimetype)
        {
            this.mimetype = mimetype;
            this.isDocument = true;
            return this;
        }

        @Getter
        @Accessors(fluent = true)
        private long size = -1;

        @Override
        public NodeBuilder size(long size)
        {
            if (size < 0) {
                throw new IllegalArgumentException("Argument 'size' should be > 0");
            }
            this.size = size;
            this.isDocument = true;
            return this;
        }

        @Override
        public NodeReference getNodeRef() {
            return new NodeReference(this.storeRefProtocol, this.storeRefIdentifier, this.uuid);
        }

        @Getter
        @Setter
        @Accessors(fluent = true, chain = true)
        private String type = Content.CONTENT;

        @Getter
        private Map<String, Serializable> properties = new HashMap<>();

        @Getter
        @Accessors(fluent = true)
        private byte[] content;

        @Getter
        @Setter
        @Accessors(fluent = true, chain = true)
        private Charset charset;

        @Override
        public NodeBuilder properties(Map<String, Serializable> properties) {
            Objects.requireNonNull(properties, "Argument 'properties' should not be null");
            this.properties.putAll(properties);
            return this;
        }

        @Override
        public NodeBuilder property(String key, String value) {
            Objects.requireNonNull(key, "Argument 'key' should not be null");
            this.properties.put(key, value);
            return this;
        }

        @Getter
        private Set<String> aspects = new LinkedHashSet<>();


        @Override
        public NodeBuilder aspects(Set<String> aspects)
        {
            Objects.requireNonNull(aspects, "Argument 'aspects' is required");
            this.aspects.forEach(this::aspect);
            return this;
        }

        @Override
        public NodeBuilder aspect(String aspect)
        {
            Objects.requireNonNull(aspect, "Argument 'aspect' is required");
            this.aspects.add(aspect);
            return this;
        }

        @Override
        public NodeBuilder name(String name)
        {
            Objects.requireNonNull(name, "Argument 'name' is required");
            this.property(Content.NAME, name);
            return this;
        }


        @Override
        public NodeBuilder content(String content) {
            return this.content(content, StandardCharsets.UTF_8);
        }

        @Override
        public NodeBuilder content(String content, Charset charset) {
            this.charset = charset;
            return this.content(content.getBytes(charset));
        }

        @Override
        public NodeBuilder content(byte[] content) {
            this.content = content;
            return this;
        }

        private List<Consumer<Node>> callbacks = new ArrayList<>();
        @Override
        public NodeBuilder callback(Consumer<Node> callback) {
            this.callbacks.add(callback);
            return this;
        }

        @Override
        public NodeBuilder createNamedReference(String name) {
            return this.callback(node -> this.context.createNamedReference(name, node));
        }

        @Override
        public NodeBuilder createNamedReference() {
            return this.callback(node -> this.context.createNamedReference(node.getName(), node));
        }
    }


}
