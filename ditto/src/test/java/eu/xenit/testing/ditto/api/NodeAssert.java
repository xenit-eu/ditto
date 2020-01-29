package eu.xenit.testing.ditto.api;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.api.model.Transaction;
import java.util.Set;
import java.util.function.Consumer;
import org.assertj.core.api.AbstractAssert;

public class NodeAssert extends AbstractAssert<NodeAssert, Node> {

    public NodeAssert(Node node) {
        super(node, NodeAssert.class);
    }

    public NodeAssert hasNodeId(long nodeId) {
        assertThat(actual.getNodeId()).isEqualTo(nodeId);
        return myself;
    }

    public NodeAssert hasName(String name) {
        assertThat(actual.getName()).isEqualTo(name);
        return myself;
    }

    public NodeAssert hasType(QName type) {
        assertThat(actual.getType())
                .as("Expected node type '%s', but is '%s'", type.toPrefixString(), actual.getType().toPrefixString())
                .isEqualTo(type);
        return myself;
    }

    public NodeAssert hasQNamePath(String qnamePath) {
        assertThat(actual.getQNamePath())
                .isEqualTo(qnamePath);

        return myself;
    }

    public NodeAssert hasParent(Consumer<NodeAssert> callback) {
        assertThat(actual.getParent())
                .satisfies(parent -> callback.accept(new NodeAssert(parent)));
        return myself;
    }

    public NodeAssert withAspects(Consumer<Set<QName>> callback) {
        assertThat(actual)
                .satisfies(n -> {
                    callback.accept(n.getAspects());
                });

        return myself;
    }

    public NodeAssert containsChildsOf(QName assocType, Node... nodes) {
        assertThat(actual.getChildNodeCollection().getChilds(assocType))
                .contains(nodes);

        return myself;
    }

    public NodeAssert containsChild(QName assocType, QName childQName) {
        assertThat(actual.getChildNodeCollection().getChild(assocType, childQName))
                .as("contains child with relation %s and name %s",
                        assocType.toPrefixString(), childQName.toPrefixString())
                .isPresent();

        return myself;
    }

}
