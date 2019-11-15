package eu.xenit.testing.ditto.alfresco;

import eu.xenit.testing.ditto.alfresco.content.FileSystemContentUrlProvider;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;

public class DefaultContext {


    private final Clock clock;
    private long nextTxnId = 1;
    private long nextNodeId = 1;

    private final Instant bootstrapInstant;

    private ContentUrlProvider contentUrlProvider = null;
    private static final ContentUrlProvider DEFAULT_CONTENTURLPROVIDER = new FileSystemContentUrlProvider();

    DefaultContext(Instant bootstrapInstant)
    {
        Objects.requireNonNull(bootstrapInstant, "Argument 'bootstrapInstant' is required");

        this.bootstrapInstant = bootstrapInstant;
        this.clock =  Clock.fixed(bootstrapInstant, ZoneId.of("UTC"));
    }

    public long nextTxnId() {
        return this.nextTxnId++;
    }

    public long peekNextTxnId() {
        return this.nextTxnId;
    }

    public void skipToTransactionId(long txnId) {
        if (txnId < this.nextTxnId) {
            String msg = String.format("Invalid parameter 'txnId' = %s - can only skip forward,"
                    + "parameter 'txnId' should be >= %s", txnId, this.nextTxnId);
            throw new IllegalArgumentException(msg);
        }

        this.nextTxnId = txnId;
    }

    public long nextNodeId() {
        return this.nextNodeId++;
    }

    public void skipToNodeId(long nodeId) {
        if (nodeId < this.nextNodeId) {
            String msg = String.format("Invalid parameter 'nodeId' = %s - can only skip forward,"
                    + "parameter 'nodeId' should be >= %s", nodeId, this.nextNodeId);
            throw new IllegalArgumentException(msg);
        }

        this.nextNodeId = nodeId;
    }

    public Instant now() {
        return Instant.now(this.clock);
    }

    public long commitTimeInMillis() {
        return now().toEpochMilli();
    }

    public void setContentUrlProvider(ContentUrlProvider contentUrlProvider) {
        if (contentUrlProvider == null) {
            contentUrlProvider = new FileSystemContentUrlProvider();
        }

        this.contentUrlProvider = contentUrlProvider;
    }

    public ContentUrlProvider getContentUrlProvider() {
        if (this.contentUrlProvider == null) {
            return DEFAULT_CONTENTURLPROVIDER;
        }

        return this.contentUrlProvider;
    }


    @Getter
    private Map<String, Node> namedReferences = new LinkedHashMap<>();

    public void createNamedReference(String name, Node node) {
        this.namedReferences.put(name, node);
    }
}
