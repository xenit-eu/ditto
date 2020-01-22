package eu.xenit.testing.ditto.internal.content;

import eu.xenit.testing.ditto.api.model.Node;

public interface ContentUrlProviderSpi {

    String createContentUrl(Node node, ContentContext context);
}
