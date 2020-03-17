package eu.xenit.testing.ditto.internal;

import static eu.xenit.testing.ditto.api.model.NodeReference.STOREREF_ID_SPACESSTORE;
import static eu.xenit.testing.ditto.api.model.NodeReference.STOREREF_PROT_WORKSPACE;

import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.internal.repository.Cursor;
import eu.xenit.testing.ditto.internal.repository.NodeRepository;
import eu.xenit.testing.ditto.util.Assert;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class DefaultNodeView implements NodeView {

    private final NodeRepository repository;
    private final Cursor cursor;

    public DefaultNodeView(NodeRepository repository, Cursor cursor) {
        this.repository = repository;
        this.cursor = cursor;
    }

    public Optional<Node> getNode(String nodeRef) {
        Assert.hasText(nodeRef, "Argument 'nodeRef' should not be empty or null");
        return this.getNode(NodeReference.parse(nodeRef));
    }

    public Optional<Node> getNode(NodeReference nodeRef) {
        Objects.requireNonNull(nodeRef, "Argument 'nodeRef' is required");
        return this.repository.getNode(nodeRef, this.cursor);
    }

    public Optional<Node> getNode(long nodeId) {
        return this.repository.getNode(nodeId, cursor);
    }

    public Stream<Node> stream() {
        return this.repository.stream(this.cursor);
    }

    @Override
    public Stream<Node> roots() {
        return this.stream()
                .filter(n -> n.getType().equals(System.STORE_ROOT))

                // check invariants
                .peek(root -> {
                    if (root.getPrimaryParentAssoc() != null) {
                        String msg = String.format("Node %s has type %s, but also has a primary parent %s ?!",
                                root, root.getType().toPrefixString(), root.getParent());
                        throw new IllegalStateException(msg);
                    }
                });
    }

    public Optional<Node> getCompanyHome() {
        return this.roots()
                .filter(r -> STOREREF_PROT_WORKSPACE.equals(r.getNodeRef().getStoreProtocol()))
                .filter(r -> STOREREF_ID_SPACESSTORE.equals(r.getNodeRef().getStoreIdentifier()))
                .findFirst()
                .flatMap(root -> root.getChildNodeCollection()
                        .getChild(System.CHILDREN, Application.createQName("company_home")));

    }
}
