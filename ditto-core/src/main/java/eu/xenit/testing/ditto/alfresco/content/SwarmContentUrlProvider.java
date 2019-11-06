package eu.xenit.testing.ditto.alfresco.content;

import eu.xenit.testing.ditto.alfresco.Node;
import eu.xenit.testing.ditto.alfresco.Node.NodeContext;
import eu.xenit.testing.ditto.alfresco.ContentUrlProvider;
import java.util.UUID;

public class SwarmContentUrlProvider implements ContentUrlProvider {

//    private final Optional<String> bucket;

    public SwarmContentUrlProvider()
    {

    }

    public String createContentData(Node node, NodeContext context) {
        StringBuilder contentUrl = new StringBuilder("swarm://")
                .append(UUID.randomUUID().toString())
                .append(".bin");

        return contentUrl.toString();
    }

}
