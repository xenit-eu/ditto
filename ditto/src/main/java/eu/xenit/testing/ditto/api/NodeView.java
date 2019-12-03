package eu.xenit.testing.ditto.api;

import java.util.Optional;
import java.util.stream.Stream;

public interface NodeView {

    Optional<Node> getNode(NodeReference nodeRef);
    Optional<Node> getNode(String nodeRef);

    Stream<Node> stream();

}
