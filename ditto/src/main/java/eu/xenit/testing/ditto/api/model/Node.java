package eu.xenit.testing.ditto.api.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public interface Node {

    long getNodeId();
    long getTxnId();
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

        static Predicate<Node> containedIn(Collection<Long> nodeIds)
        {
            if (nodeIds == null) {
                return always(true);
            }

            if (nodeIds.isEmpty()) {
                return always(false);
            }

            HashSet<Long> set = new HashSet<>(nodeIds);
            return (node) -> set.contains(node.getNodeId());
        }
    }

}
