package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.BuilderConfigurator;
import eu.xenit.testing.ditto.api.content.SwarmContentServiceCustomizer;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.content.FileSystemContentUrlProvider;
import eu.xenit.testing.ditto.internal.content.SwarmContentUrlProvider;
import eu.xenit.testing.ditto.internal.content.SwarmContentServiceConfiguration;
import java.util.function.Consumer;

class DefaultBuilderConfigurator implements BuilderConfigurator {

    private final RootContext context;

    DefaultBuilderConfigurator(RootContext context) {

        this.context = context;
    }

    @Override
    public BuilderConfigurator resetDefaultContentUrlProvider() {
        this.context.setContentUrlProvider(null);
        return this;
    }

    @Override
    public BuilderConfigurator useFileSystemContentService() {
        this.context.setContentUrlProvider(new FileSystemContentUrlProvider());
        return this;
    }

    @Override
    public BuilderConfigurator useSwarmContentService(Consumer<SwarmContentServiceCustomizer> customizer) {
        SwarmContentServiceConfiguration config = new SwarmContentServiceConfiguration();
        customizer.accept(config);

        SwarmContentUrlProvider swarm = new SwarmContentUrlProvider(config.bucket());
        this.context.setContentUrlProvider(swarm);

        return this;
    }

    @Override
    public BuilderConfigurator setDefaultParentNode(Node node) {
        this.context.setDefaultParentNode(node);
        return this;
    }

    @Override
    public BuilderConfigurator setDefaultChildAssocType(QName qname) {
        this.context.setDefaultChildAssocType(qname);
        return this;
    }

    @Override
    public BuilderConfigurator skipToTxnId(long newTxnSeqId) {
        this.context.skipToTransactionId(newTxnSeqId);
        return this;
    }

    @Override
    public BuilderConfigurator skipToNodeId(long newNodeSeqId) {
        this.context.skipToNodeId(newNodeSeqId);
        return this;
    }
}
