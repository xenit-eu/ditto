package eu.xenit.testing.ditto.alfresco;

import eu.xenit.testing.ditto.alfresco.Node.NodeContext;

public interface ContentUrlProvider {

    String createContentData(Node node, NodeContext context);
}
