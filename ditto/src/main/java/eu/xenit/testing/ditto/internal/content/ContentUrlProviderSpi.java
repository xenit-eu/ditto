package eu.xenit.testing.ditto.internal.content;

import eu.xenit.testing.ditto.api.Node;

public interface ContentUrlProviderSpi {

    String createContentData(Node node, ContentContext context);
}
