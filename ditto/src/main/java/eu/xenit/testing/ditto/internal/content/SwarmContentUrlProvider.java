package eu.xenit.testing.ditto.internal.content;

import eu.xenit.testing.ditto.api.Node;
import java.util.UUID;

public class SwarmContentUrlProvider implements ContentUrlProviderSpi {

//    private final Optional<String> bucket;

    public SwarmContentUrlProvider()
    {

    }

    public SwarmContentUrlProvider(String bucket)
    {
        if (bucket != null && bucket.length() > 0) {
            throw new UnsupportedOperationException("swarm bucket not yet implemented");
        }
    }

    public String createContentUrl(Node node, ContentContext context) {
        // TODO add logic for buckets ?!
        return String.format("swarm://%s.bin", UUID.randomUUID().toString());
    }

}
