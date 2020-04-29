package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.util.Assert;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

public interface NodeView {

    Optional<Node> getNode(long nodeId);
    Optional<Node> getNode(NodeReference nodeRef);

    default Optional<Node> getNode(String nodeRef) {
        Assert.hasText(nodeRef, "Argument 'nodeRef' should not be empty or null");
        return this.getNode(NodeReference.parse(nodeRef));
    }

    Stream<Node> allNodes();
    Stream<Node> rootNodes();

    Optional<Node> getCompanyHome();

    @Deprecated
    default Stream<Node> roots() {
        return this.rootNodes();
    }

    @Deprecated
    default Stream<Node> stream() {
        return this.allNodes();
    }


}
