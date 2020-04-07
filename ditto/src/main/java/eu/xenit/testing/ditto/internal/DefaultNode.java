package eu.xenit.testing.ditto.internal;

import static eu.xenit.testing.ditto.api.model.NodeReference.STOREREF_ID_SPACESSTORE;
import static eu.xenit.testing.ditto.api.model.NodeReference.STOREREF_PROT_WORKSPACE;

import eu.xenit.testing.ditto.api.NodeCustomizer;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.MLText;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.ParentChildAssoc;
import eu.xenit.testing.ditto.api.model.PeerAssoc;
import eu.xenit.testing.ditto.api.model.PeerAssocCollection;
import eu.xenit.testing.ditto.api.model.PeerAssocCollection.Type;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
    private final long txnId;

    @Getter
    @NonNull
    private final NodeReference nodeRef;

    @Getter
    @NonNull
    private final QName type;

    @Getter
    @NonNull
    private final QName qName;

    @Getter
    private final ParentChildAssoc primaryParentAssoc;

    @Getter
    private final DefaultNodeProperties properties;

    @Getter
    private final DefaultParentChildNodeCollection childNodeCollection;

    @Getter
    private final DefaultParentChildNodeCollection parentNodeCollection;

    @Getter
    private final PeerAssocCollection sourceAssociationCollection;

    @Getter
    private final PeerAssocCollection targetAssociationCollection;

    @Getter
    private final Set<QName> aspects;

    private static NodeInitializer init = new NodeInitializer();

    private DefaultNode(NodeBuilder builder) {
        this.nodeId = builder.nodeId;
        this.txnId = builder.txnId;
        this.nodeRef = builder.nodeRef();
        this.type = builder.type;
        this.properties = new DefaultNodeProperties(builder.properties);
        this.qName = builder.qname != null ? builder.qname : Content.createQName(this.getName());
        this.aspects = new HashSet<>(builder.aspects);
        this.childNodeCollection = new DefaultParentChildNodeCollection(this);
        this.sourceAssociationCollection = new DefaultPeerAssocCollection(Type.SOURCE, builder.sourceAssociations);
        this.targetAssociationCollection = new DefaultPeerAssocCollection(Type.TARGET, builder.targetAssociations);

        if (builder.context.getParent() != null && builder.context.getParentChildAssocType() != null) {
            this.primaryParentAssoc = new DefaultParentChildAssoc(builder.context.getParent(),
                    builder.context.getParentChildAssocType(), this, true);
            this.parentNodeCollection = new DefaultParentChildNodeCollection(primaryParentAssoc.getParent(),
                    Collections.singletonList(primaryParentAssoc));
        } else {
            this.primaryParentAssoc = null;
            this.parentNodeCollection = null;
        }

        init.accept(this, builder.context);
    }

    public static NodeBuilder builder(TransactionContext context, Node parent,
            QName assocType) {
        return new NodeBuilder(context, parent, assocType);
    }

    @Override
    public String getName() {
        return this.getProperties()
                .get(Content.NAME)
                .map(Object::toString)
                .orElse(this.getNodeRef().getUuid());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[id=" + this.nodeId + "; nodeRef=" + this.getNodeRef() + "]";
    }

    public static class NodeContext implements ContentContext {

        private final TransactionContext txnContext;

        @Getter(AccessLevel.PACKAGE)
        private final Map<QName, ContentDataBuilder> contentDataMap = new LinkedHashMap<>();

        @Getter
        private final Instant instant;

        @Getter
        private final Locale defaultLocale;

        @Getter
        private final Node parent;

        @Getter
        private final QName parentChildAssocType;


        private NodeContext(TransactionContext txnContext, Node parent, QName parentChildAssocType) {

            this.txnContext = txnContext;
            this.instant = txnContext.now();
            this.defaultLocale = txnContext.defaultLocale();
            this.parent = parent;
            this.parentChildAssocType = parentChildAssocType;
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

        void skipToContentDataId(long contentDataId) {
            txnContext.skipToContentDataId(contentDataId);
        }

        long nextContentDataId() {
            return txnContext.nextContentDataId();
        }


        @Deprecated
        void createNamedReference(String name, Node node) {
            this.txnContext.createNamedReference(name, node);
        }

        QName resolveQName(String qname) {
            return this.txnContext.resolveQName(qname);
        }

        void onNodeSaved(DefaultNode node) {
            this.txnContext.onNodeSaved(node);
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


        private NodeBuilder(TransactionContext context, Node parent, QName parentChildAssocType) {
            this.nodeId = context.nextNodeId();
            this.txnId = context.getTxnId();
            this.context = new NodeContext(context, parent, parentChildAssocType);
        }

        DefaultNode build() {
            DefaultNode node = new DefaultNode(this);

            this.callbacks.forEach(callback -> callback.accept(node));

            this.context.onNodeSaved(node);

            return node;
        }

        @Getter
        private final long nodeId;

        @Getter
        private final long txnId;

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
        @Accessors(fluent = true)
        private QName type = Content.OBJECT;

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
        @Accessors(fluent = true)
        private QName qname;

        @Override
        public NodeCustomizer qname(String qname) {
            return this.qname(this.context.resolveQName(qname));
        }

        @Override
        public NodeCustomizer qname(QName qname) {
            this.qname = qname;
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
        public NodeBuilder property(String key, Serializable value) {
            Objects.requireNonNull(key, "Argument 'key' should not be null");

            QName qname = this.context.resolveQName(key);
            this.properties.put(qname, value);

            return this;
        }

        @Override
        public NodeBuilder property(QName key, Serializable value) {
            Objects.requireNonNull(key, "Argument 'key' should not be null");
            this.properties.put(key, value);
            return this;
        }

        @Override
        public NodeCustomizer mlProperty(QName key, String value) {
            return mlProperty(key, context.defaultLocale, value);
        }

        @Override
        public NodeCustomizer mlProperty(QName key, Locale locale, String value) {
            Objects.requireNonNull(locale, "Argument 'locale' should not be null");
            if (this.properties.containsKey(key)) {
                Serializable existing = this.properties.get(key);
                if (!(existing instanceof MLText)) {
                    throw new IllegalStateException(String.format("Property '%s' is not of type d:mltext", existing));
                }
                this.properties.put(key, ((MLText) existing).put(locale, value));
            } else {
                this.properties.put(key, MLText.create(locale, value));
            }
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

        @Getter
        List<PeerAssoc> sourceAssociations = new ArrayList<>();

        @Override
        public NodeCustomizer sourceAssociation(PeerAssoc sourceAssociation) {
            Objects.requireNonNull(sourceAssociation, "Argument 'sourceAssociation' is required");
            sourceAssociations.add(sourceAssociation);
            return this;
        }

        @Override
        public NodeCustomizer sourceAssociation(Node sourceNode, QName associationTypeQName) {
            Objects.requireNonNull(sourceNode, "Argument 'sourceNode' is required");
            Objects.requireNonNull(associationTypeQName, "Argument 'associationTypeQName' is required");
            sourceAssociations.add(new DefaultPeerAssoc(sourceNode, new DefaultNode(this), associationTypeQName));
            return this;
        }

        @Override
        public NodeCustomizer sourceAssociations(List<PeerAssoc> sourceAssociations) {
            Objects.requireNonNull(sourceAssociations, "Argument 'sourceAssociations' is required");
            this.sourceAssociations = sourceAssociations;
            return this;
        }

        @Getter
        List<PeerAssoc> targetAssociations = new ArrayList<>();

        @Override
        public NodeCustomizer targetAssociation(PeerAssoc targetAssociation) {
            Objects.requireNonNull(targetAssociation, "Argument 'targetAssociation' is required");
            targetAssociations.add(targetAssociation);
            return this;
        }

        @Override
        public NodeCustomizer targetAssociation(Node targetNode, QName associationTypeQName) {
            Objects.requireNonNull(targetNode, "Argument 'targetNode' is required");
            Objects.requireNonNull(associationTypeQName, "Argument 'associationTypeQName' is required");
            sourceAssociations.add(new DefaultPeerAssoc(new DefaultNode(this), targetNode, associationTypeQName));
            return this;
        }

        @Override
        public NodeCustomizer targetAssociations(List<PeerAssoc> targetAssociations) {
            this.targetAssociations = targetAssociations;
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
        @Deprecated
        public NodeBuilder createNamedReference(String name) {
            return this.callback(node -> this.context.createNamedReference(name, node));
        }

        @Override
        @Deprecated
        public NodeBuilder createNamedReference() {
            return this.callback(node -> this.context.createNamedReference(node.getName(), node));
        }
    }
}
