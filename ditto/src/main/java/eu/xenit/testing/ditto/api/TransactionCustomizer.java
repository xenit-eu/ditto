package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.content.ContentServiceConfigurator;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;
import java.util.function.Consumer;

public interface TransactionCustomizer extends ContentServiceConfigurator<TransactionCustomizer> {

    // Consider moving this in to a configurator object, together with the ContentServiceConfigurator
    TransactionCustomizer skipToNodeId(long nodeId);

    Node addNode(Node parent, QName assocType, Consumer<NodeCustomizer> customizer);
    Node addNode(Node parent, Consumer<NodeCustomizer> customizer);
    Node addNode(Consumer<NodeCustomizer> customizer);

    Node addRoot(Consumer<NodeCustomizer> callback);
    Node addDocument(Node parent, Consumer<NodeCustomizer> callback);
    Node addFolder(Node parent, Consumer<NodeCustomizer> callback);

    @Deprecated
    Node getNodeByNodeRef(String nodeRef);

    default Node addRoot() {
        return this.addRoot((r) -> {});
    }

}
