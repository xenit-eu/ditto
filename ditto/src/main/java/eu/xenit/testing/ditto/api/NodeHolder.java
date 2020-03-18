package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.model.Node;

public final class NodeHolder {

    private Node node;

    public void set(Node node) {
        this.node = node;
    }

    public Node get() {
        return this.node;
    }

    public void unset() {
        this.node = null;
    }
}
