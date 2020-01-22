package eu.xenit.testing.ditto.internal;

import static eu.xenit.testing.ditto.api.model.NodeReference.STOREREF_ID_SPACESSTORE;
import static eu.xenit.testing.ditto.api.model.NodeReference.STOREREF_PROT_WORKSPACE;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.NodeCustomizer;
import eu.xenit.testing.ditto.api.model.NodeProperties;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionContext;
import eu.xenit.testing.ditto.internal.content.ContentContext;
import eu.xenit.testing.ditto.internal.content.ContentUrlProviderSpi;
import eu.xenit.testing.ditto.util.MimeTypes;
import eu.xenit.testing.ditto.util.StringUtils;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
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
    private final QName type;

    @Getter
    private final NodeProperties properties;

    @Getter
    private final Set<QName> aspects;

    private static NodeInitializer init = new NodeInitializer();

    private DefaultNode(NodeBuilder builder) {
        this.nodeId = builder.nodeId;
        this.nodeRef = builder.nodeRef();
        this.type = builder.type;
        this.properties = new DefaultNodeProperties(builder.properties);
        this.aspects = new HashSet<>(builder.aspects);

        init.accept(this, builder.context);
    }

    public static NodeBuilder builder(TransactionContext context) {
        return new NodeBuilder(context);
    }

    @Override
    public String getName() {
        return (String) this.getProperties().get(Content.NAME);
    }

    public static class NodeContext implements ContentContext {

        private final TransactionContext txnContext;

        @Getter(AccessLevel.PACKAGE)
        private final Map<QName, ContentDataBuilder> contentDataMap = new LinkedHashMap<>();

        @Getter
        private final Instant instant;

        private NodeContext(TransactionContext txnContext) {
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

        ContentDataBuilder getContentData(boolean createIfAbsent) {
            return this.getContentDataByName(Content.CONTENT, createIfAbsent);
        }

        ContentDataBuilder getContentDataByName(QName propertyName, boolean createIfAbsent) {
            Objects.requireNonNull(propertyName, "Argument 'propertyName' is required");

            if (createIfAbsent) {
                return this.contentDataMap.computeIfAbsent(propertyName, (QName key) -> new ContentDataBuilder());
            }

            return this.contentDataMap.get(propertyName);
        }


        public void createNamedReference(String name, Node node) {
            this.txnContext.createNamedReference(name, node);
        }

        public QName resolveQName(String qname) {
            return this.txnContext.resolveQName(qname);
        }
    }

    @Data
    @Accessors(fluent = true)
    static class ContentDataBuilder {

        private byte[] data;
        private String contentUrl;
        private String mimetype = MimeTypes.APPLICATION_OCTET_STREAM;
        private long size;

        private String encoding;
        private String locale;

        public Charset getEncodingOrDefault() {
            return StringUtils.hasText(encoding) ? Charset.forName(this.encoding) : Charset.defaultCharset();
        }

    }

    @Accessors(fluent = true, chain = true)
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

        @Override
        public NodeReference nodeRef() {
            return new NodeReference(this.storeRefProtocol, this.storeRefIdentifier, this.uuid);
        }

        @Getter
        @Accessors(fluent = true, chain = true)
        private QName type = Content.CONTENT;

        @Override
        public NodeCustomizer type(QName type) {
            this.type = type;
            return this;
        }

        @Override
        public NodeCustomizer type(String type) {
            this.type = this.context.resolveQName(type);
            return this;
        }

        @Getter
        private Map<QName, Serializable> properties = new HashMap<>();

        @Getter
        @Setter
        @Accessors(fluent = true, chain = true)
        private Charset charset;

        @Override
        public NodeBuilder properties(Map<QName, Serializable> properties) {
            Objects.requireNonNull(properties, "Argument 'properties' should not be null");
            this.properties.putAll(properties);
            return this;
        }

        @Override
        public NodeBuilder property(String key, String value) {
            Objects.requireNonNull(key, "Argument 'key' should not be null");

            QName qname = this.context.resolveQName(key);
            this.properties.put(qname, value);

            return this;
        }

        @Override
        public NodeBuilder property(QName key, String value) {
            Objects.requireNonNull(key, "Argument 'key' should not be null");
            this.properties.put(key, value);
            return this;
        }

        @Getter
        private Set<QName> aspects = new LinkedHashSet<>();


        @Override
        public NodeBuilder aspects(Set<String> aspects) {
            Objects.requireNonNull(aspects, "Argument 'aspects' is required");
            this.aspects = aspects.stream().map(this.context::resolveQName).collect(Collectors.toSet());
            return this;
        }

        @Override
        public NodeBuilder aspect(QName aspect) {
            Objects.requireNonNull(aspect, "Argument 'aspect' is required");
            this.aspects.add(aspect);
            return this;
        }

        @Override
        public NodeBuilder aspect(String aspect) {
            Objects.requireNonNull(aspect, "Argument 'aspect' is required");
            this.aspects.add(this.context.resolveQName(aspect));
            return this;
        }

        @Override
        public NodeBuilder name(String name) {
            Objects.requireNonNull(name, "Argument 'name' is required");
            this.property(Content.NAME, name);
            return this;
        }

        public boolean isDocument() {
            return !this.context.getContentDataMap().isEmpty();
        }

        public NodeBuilder isDocument(boolean isDoc) {
            if (isDoc == !this.context.getContentDataMap().isEmpty()) {
                return this;
            }

            if (isDoc) {
                // Creating a default content property
                this.context.getContentData(true);
            } else {
                // Clearing all content properties
                this.context.getContentDataMap().clear();
            }
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
            ContentDataBuilder contentBuilder = this.context.getContentData(true);
            contentBuilder.data(content);
            return this;
        }

        @Override
        public byte[] content() {
            ContentDataBuilder contentData = this.context.getContentData(false);
            return (contentData == null) ? null : contentData.data();
        }

        @Override
        public String mimetype() {
            ContentDataBuilder contentData = this.context.getContentData(false);
            return (contentData == null) ? null : contentData.mimetype();
        }

        @Override
        public NodeBuilder mimetype(String mimetype) {
            this.context.getContentData(true).mimetype(mimetype);
            return this;
        }

        @Override
        public long size() {
            ContentDataBuilder contentData = this.context.getContentData(false);
            return (contentData == null) ? -1 : contentData.size();
        }

        @Override
        public NodeBuilder size(long size) {
            this.context.getContentData(true).size(size);
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
