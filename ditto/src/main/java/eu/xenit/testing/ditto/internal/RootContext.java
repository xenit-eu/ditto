package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.BootstrapConfiguration;
import eu.xenit.testing.ditto.api.BuilderConfigurator;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.content.ContentUrlProviderSpi;
import eu.xenit.testing.ditto.internal.content.FileSystemContentUrlProvider;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class RootContext {

    private final Clock clock;
    private final DictionaryService dictionary;

    private long nextTxnId = 1;
    private long nextNodeId = 1;
    private long nextContentDataId = 1;

    private final Locale defaultLocale;

    private ContentUrlProviderSpi contentUrlProvider = null;
    private static final ContentUrlProviderSpi DEFAULT_CONTENTURLPROVIDER = new FileSystemContentUrlProvider();

    RootContext(BootstrapConfiguration bootstrapConfig)
    {
        Objects.requireNonNull(bootstrapConfig, "Argument 'bootstrapConfig' is required");

        Instant bootstrapInstant = bootstrapConfig.getBootstrapInstant().truncatedTo(ChronoUnit.MILLIS);
        this.clock =  Clock.fixed(bootstrapInstant, ZoneId.of("UTC"));

        this.defaultLocale = bootstrapConfig.getDefaultLocale();

        this.dictionary = new DictionaryService();
        bootstrapConfig.getNamespaces().forEach(this.dictionary::registerNamespace);
    }

    RootContext(RootContext parentContext) {
        this.clock = parentContext.clock;

        // FIXME dictionary contains state and should be properly versioned with a cursor ?
        this.dictionary = parentContext.dictionary;

        this.nextTxnId = parentContext.nextTxnId;
        this.nextNodeId = parentContext.nextNodeId;
        this.nextContentDataId = parentContext.nextContentDataId;

        this.defaultLocale = parentContext.defaultLocale;
        this.contentUrlProvider = parentContext.contentUrlProvider;
    }

    long nextTxnId() {
        return this.nextTxnId++;
    }

    public long peekNextTxnId() {
        return this.nextTxnId;
    }

    void skipToTransactionId(long txnId) {
        if (txnId < this.nextTxnId) {
            String msg = String.format("Invalid parameter 'txnId' = %s - can only skip forward,"
                    + "parameter 'txnId' should be >= %s", txnId, this.nextTxnId);
            throw new IllegalArgumentException(msg);
        }

        this.nextTxnId = txnId;
    }

    long nextNodeId() {
        return this.nextNodeId++;
    }

    void skipToNodeId(long nodeId) {
        if (nodeId < this.nextNodeId) {
            String msg = String.format("Invalid parameter 'nodeId' = %s - can only skip forward,"
                    + "parameter 'nodeId' should be >= %s", nodeId, this.nextNodeId);
            throw new IllegalArgumentException(msg);
        }

        this.nextNodeId = nodeId;
    }

    long nextContentDataId() {
        return this.nextContentDataId++;
    }

    void skipToContentDataId(long contentDataId) {
        if (contentDataId < this.nextContentDataId) {
            String msg = String.format("Invalid parameter 'contentDataId' = %s - can only skip forward,"
                    + "parameter 'contentDataId' should be >= %s", contentDataId, this.nextNodeId);
            throw new IllegalArgumentException(msg);
        }

        this.nextContentDataId = contentDataId;
    }

    Instant now() {
        return Instant.now(this.clock);
    }

    public Locale defaultLocale() {
        return this.defaultLocale;
    }

    long commitTimeInMillis() {
        return now().toEpochMilli();
    }

    void setContentUrlProvider(ContentUrlProviderSpi contentUrlProvider) {
        if (contentUrlProvider == null) {
            contentUrlProvider = new FileSystemContentUrlProvider();
        }

        this.contentUrlProvider = contentUrlProvider;
    }

    ContentUrlProviderSpi getContentUrlProvider() {
        if (this.contentUrlProvider == null) {
            return DEFAULT_CONTENTURLPROVIDER;
        }

        return this.contentUrlProvider;
    }


    @Deprecated
    @Getter
    private Map<String, Node> namedReferences = new LinkedHashMap<>();

    @Deprecated
    void createNamedReference(String name, Node node) {
        this.namedReferences.put(name, node);
    }

    QName resolveQName(String qname) {
        return this.dictionary.resolveQName(qname);
    }

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private QName defaultChildAssocType = Content.CONTAINS;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private Node defaultParentNode = null;

    private Map<NodeReference, Node> nodes = new HashMap<>();

    void onNodeSaved(Node node) {
        Objects.requireNonNull(node, "Argument 'node' is required");
        this.nodes.put(node.getNodeRef(), node);
    }

    Node getNodeByNodeRef(NodeReference nodeRef) {
        return nodes.get(nodeRef);
    }
}
