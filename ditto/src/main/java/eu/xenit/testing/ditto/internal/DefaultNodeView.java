package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.Transaction;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.internal.mvcc.Cursor;
import eu.xenit.testing.ditto.internal.mvcc.RecordLogEntry;
import eu.xenit.testing.ditto.util.Assert;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultNodeView implements NodeView {

    private final HashMap<NodeReference, Node> nodesByNodeRef = new LinkedHashMap<>();
    private final HashMap<Long, Node> nodesById = new LinkedHashMap<>();

    public DefaultNodeView(Cursor<Transaction> cursor) {

        this.process(cursor.getHead());
    }

    private void process(RecordLogEntry<Transaction> head) {
        this.processRecursive(head);
    }

    private void processRecursive(RecordLogEntry<Transaction> record) {

        Objects.requireNonNull(record, "record cannot be null");

        RecordLogEntry<Transaction> parent = record.getParent();
        if (parent == null) {
            return;
        }

        processRecursive(parent);

        this.process(record.getData());
    }

    private void process(Transaction txn) {
        log.debug("Replaying {} - with {} writes and {} deletes", txn, txn.getUpdated().size(), txn.getDeleted().size());

        txn.getUpdated().forEach(this::add);
        txn.getDeleted().forEach(this::delete);
    }

    private void add(Node node) {
        this.nodesById.put(node.getNodeId(), node);
        this.nodesByNodeRef.put(node.getNodeRef(), node);
    }

    private void delete(Node node) {
        Long nodeId = node.getNodeId();

        Node removed = this.nodesById.remove(nodeId);
        if (removed == null) {
            String exMsg = String.format("Node with id %s is not found in view", nodeId);
            throw new IllegalArgumentException(exMsg);
        }
        if (!Objects.equals(node.getNodeRef(), removed.getNodeRef())) {
            String msg = String.format("Aggregate store inconsistency detected, noderef mismatch: %s vs %s",
                    node.getNodeRef(), removed.getNodeRef());
            throw new IllegalArgumentException(msg);
        }

        this.nodesByNodeRef.remove(node.getNodeRef());
    }

    public Optional<Node> getNode(String nodeRef)
    {
        Assert.hasText(nodeRef, "Argument 'nodeRef' should not be empty or null");
        return this.getNode(NodeReference.parse(nodeRef));
    }

    public Optional<Node> getNode(NodeReference nodeRef) {
        Objects.requireNonNull(nodeRef, "Argument 'nodeRef' is required");
        return Optional.of(this.nodesByNodeRef.get(nodeRef));
    }

    public Stream<Node> stream() {
        return this.nodesById.values().stream();
    }
}
