package eu.xenit.testing.ditto.internal.repository;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.MockRootContext;
import eu.xenit.testing.ditto.internal.NodeTestUtil;
import eu.xenit.testing.ditto.internal.RootContext;
import eu.xenit.testing.ditto.internal.record.RecordLog;
import eu.xenit.testing.ditto.internal.record.RecordLogEntry;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NodeRepositoryTest {


    private NodeRepoTestSetup newTestSetup() {
        RootContext ctx = new MockRootContext();
        Transaction txn = NodeTestUtil.txn(ctx, t -> {
            Node root = t.addRoot();
            Node folder = t.addFolder(root, n -> n.name("Folder"));
            t.addDocument(folder, n -> {
                n.uuid("59780a5c-653a-11ea-99e4-af651f4e9eca");
                n.name("README.md");
            });
        });

        NodeRepository nodeRepository = new NodeRepository();
        RecordLogEntry<Transaction> head = new RecordLog<Transaction>().process(txn, nodeRepository);
        Cursor cursor = new Cursor(head);

        return new NodeRepoTestSetup(cursor, nodeRepository);
    }

    @Test
    void processTransactionWithUpdates() {
        NodeRepoTestSetup setup = newTestSetup();
        Cursor cursor = setup.cursor;
        NodeRepository nodeRepository = setup.nodeRepository;

        assertThat(setup.cursor.chain()).contains(1L);

        // check index by node-id data-structure
        assertThat(setup.nodeRepository.getNodesByNodeId())
                .hasSize(3)
                .hasEntrySatisfying(2L, tuple -> {
                    Node n = tuple.walk(cursor.chain()).data;
                    assertThat(n.getNodeId()).isEqualTo(2L);
                    assertThat(n.getName()).isEqualTo("Folder");
                })
                .hasEntrySatisfying(3L, tuple -> {
                    Node n = tuple.walk(cursor.chain()).data;
                    assertThat(n.getNodeId()).isEqualTo(3L);
                    assertThat(n.getName()).isEqualTo("README.md");
                });

        // check index by node-ref data-structure
        assertThat(nodeRepository.getNodesByNodeRef()).hasSize(3);
    }

    @Test
    void getNodeByNodeId() {
        NodeRepoTestSetup setup = newTestSetup();
        NodeRepository nodeRepository = setup.nodeRepository;

        Optional<Node> node = nodeRepository.getNode(3L, setup.cursor);
        assertThat(node)
                .isPresent()
                .hasValueSatisfying(n -> {
                    assertThat(n.getNodeId()).isEqualTo(3L);
                    // TODO check version once we have multiple versions supported
                });
    }

    @Test
    void getNodeByNodeRef() {
        NodeRepoTestSetup setup = newTestSetup();
        NodeRepository nodeRepository = setup.nodeRepository;

        NodeReference nodeRef = NodeReference.parse("workspace://SpacesStore/59780a5c-653a-11ea-99e4-af651f4e9eca");
        Optional<Node> node = nodeRepository.getNode(nodeRef, setup.cursor);

        assertThat(node)
                .isPresent()
                .hasValueSatisfying(n -> {
                    assertThat(n.getNodeId()).isEqualTo(3L);
                    // TODO check version once we have multiple versions supported
                });
    }

    private class NodeRepoTestSetup {

        private NodeRepoTestSetup(Cursor cursor, NodeRepository nodeRepository) {
            this.cursor = cursor;
            this.nodeRepository = nodeRepository;
        }

        final Cursor cursor;
        final NodeRepository nodeRepository;
    }

}