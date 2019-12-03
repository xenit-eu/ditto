package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.content.ContentServiceConfigurer;
import java.util.function.Consumer;

public interface TransactionCustomizer extends ContentServiceConfigurer<TransactionCustomizer> {

    TransactionCustomizer skipToNodeId(long nodeId);

    TransactionCustomizer addNode(Consumer<NodeCustomizer> customizer);

    TransactionCustomizer addNode();

    TransactionCustomizer addDocument(String name);

    TransactionCustomizer addDocument(String name, Consumer<NodeCustomizer> callback);

    TransactionCustomizer addFolder(String name);

    TransactionCustomizer addFolder(String name, Consumer<NodeCustomizer> callback);
}
