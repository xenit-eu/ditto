package eu.xenit.testing.ditto.internal.repository;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.record.RecordChain;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;

public class NodeRepository extends DataRepositoryBase implements TransactionProcessor {

    @Getter(AccessLevel.PACKAGE) // accessor for testing
    private final HashMap<Long, RecordTuple<Node>> nodesByNodeId = new LinkedHashMap<>();

    @Getter(AccessLevel.PACKAGE) // accessor for testing
    private final HashMap<NodeReference, RecordTuple<Node>> nodesByNodeRef = new LinkedHashMap<>();

    @Override
    public void process(long recordId, RecordChain chain, Transaction txn) {

        txn.getUpdated().forEach(node -> {
            this.store(chain, recordId, node)
                    .withIndex(this.nodesByNodeId,  (tuple) -> tuple.data.getNodeId())
                    .withIndex(this.nodesByNodeRef,  (tuple) -> tuple.data.getNodeRef());
        });

        txn.getDeleted().forEach(node -> {
            // TODO
            throw new UnsupportedOperationException("not yet implemented");
        });
    }

    public Optional<Node> getNode(long nodeId, Cursor cursor) {
        return this.load(this.nodesByNodeId, nodeId, cursor.chain());
    }

    public Optional<Node> getNode(NodeReference nodeRef, Cursor cursor) {
        return this.load(this.nodesByNodeRef, nodeRef, cursor.chain());
    }

    public Stream<Node> stream(Cursor cursor) {
        return this.nodesByNodeId.values()
                .stream()
                .map(record -> record.walk(cursor.chain()))
                .filter(record -> !record.deleted)
                .map(record -> record.data);
    }
}
