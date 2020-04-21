package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.content.ContentServiceConfigurator;

public interface BuilderConfigurator extends
        ContentServiceConfigurator<BuilderConfigurator>,
        NodeConfigurator<BuilderConfigurator> {

    BuilderConfigurator skipToTxnId(long newTxnSeqId);
    BuilderConfigurator skipToNodeId(long newNodeSeqId);

}
