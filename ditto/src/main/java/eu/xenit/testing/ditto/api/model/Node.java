package eu.xenit.testing.ditto.api.model;

import java.util.Set;
import java.util.function.Predicate;

public interface Node {

    long getNodeId();
    NodeReference getNodeRef();
    QName getType();

    String getName();

    NodeProperties getProperties();
    Set<QName> getAspects();

    interface Filters {
        static <T> Predicate<T> always(boolean value) {
            return (var0) -> value;
        }

        static Predicate<Node> minNodeIdInclusive(Long minNodeId) {
            return minNodeId == null ? always(true) : (node) -> node.getNodeId() >= minNodeId;
        }

        static Predicate<Node> maxNodeIdInclusive(Long maxNodeId) {
            return maxNodeId == null ? always(true) : (node) -> node.getNodeId() <= maxNodeId;
        }
    }

}
