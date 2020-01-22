package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.BootstrapConfiguration;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.content.ContentUrlProviderSpi;
import eu.xenit.testing.ditto.internal.content.FileSystemContentUrlProvider;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;

public class RootContext {

    private final Clock clock;
    private final DictionaryService dictionary = new DictionaryService();

    private long nextTxnId = 1;
    private long nextNodeId = 1;

    private final Instant bootstrapInstant;

    private ContentUrlProviderSpi contentUrlProvider = null;
    private static final ContentUrlProviderSpi DEFAULT_CONTENTURLPROVIDER = new FileSystemContentUrlProvider();

    RootContext(BootstrapConfiguration bootstrapConfig)
    {
        Objects.requireNonNull(bootstrapConfig, "Argument 'bootstrapConfig' is required");

        this.bootstrapInstant = bootstrapConfig.getBootstrapInstant();
        this.clock =  Clock.fixed(bootstrapInstant, ZoneId.of("UTC"));


        bootstrapConfig.getNamespaces().forEach(this.dictionary::registerNamespace);
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

    Instant now() {
        return Instant.now(this.clock);
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


    @Getter
    private Map<String, Node> namedReferences = new LinkedHashMap<>();

    void createNamedReference(String name, Node node) {
        this.namedReferences.put(name, node);
    }

    public QName resolveQName(String qname) {
        return this.dictionary.resolveQName(qname);
    }
}
