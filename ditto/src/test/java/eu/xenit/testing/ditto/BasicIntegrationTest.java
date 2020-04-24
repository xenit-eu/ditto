package eu.xenit.testing.ditto;

import static eu.xenit.testing.ditto.internal.DittoAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.ContentView;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.NodeReference;
import org.junit.jupiter.api.Test;

class BasicIntegrationTest {

    private final NodeReference NODEREF_FOO_TXT = NodeReference.newNodeRef();

    private AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco()
            .configure(config -> {
                config.skipToTxnId(12345L);
                config.skipToNodeId(4321L);
            })
            .addTransaction(txn -> {
                txn.addNode(doc -> {
                    doc.nodeRef(NODEREF_FOO_TXT);
                    doc.type(Content.CONTENT);
                    doc.name("foo.txt");
                    doc.content("foobar");
                    doc.property("cm:description", "Test description");
                });
            })
            .build();

    @Test
    void checkTransactionViewBasics() {
        // Sanity check transaction-view
        assertThat(dataSet.getTransactionView())
                .isNotNull()
                .satisfies(txnView -> {
                    long lastTxnId = txnView.getLastTxnId();
                    assertThat(lastTxnId).isEqualTo(12345L);
                    assertThat(txnView.getTransactionById(lastTxnId))
                            .hasValueSatisfying(lastTxn -> {
                                assertThat(lastTxn).isNotNull();
                                assertThat(lastTxn.getUpdated())
                                        // TODO this will break once folders-assocs are implemented
                                        .hasOnlyOneElementSatisfying(n -> {
                                            assertThat(n.getName()).isEqualTo("foo.txt");
                                        });
                            });

                    assertThat(txnView.stream()).isNotNull().isNotEmpty()
                            .last().satisfies(txn -> assertThat(txn.getId()).isEqualTo(12345L));
                });
    }

    @Test
    void checkNodeViewBasics() {
        // Sanity check node-view
        assertThat(dataSet.getNodeView())
                .as("%s should not be null", NodeView.class.getSimpleName())
                .isNotNull()
                .satisfies(nodeView -> {
                    assertThat(nodeView.stream())
                            .filteredOn(node -> node.getName().equalsIgnoreCase("foo.txt"))
                            .hasSize(1);
                });
    }

    @Test
    void checkContentViewBasics() {
        // Sanity check content-view
        assertThat(dataSet.getContentView())
                .as("%s should not be null", ContentView.class.getSimpleName())
                .isNotNull()
                .satisfies(contentView -> {
                    assertThat(contentView.getContent(NODEREF_FOO_TXT))
                            .isPresent()
                            .hasValueSatisfying(stream -> {
                                assertThat(stream).hasContent("foobar");
                            });
                });
    }

    @Test
    void testContinuation() {
        NodeReference nodeRef = NodeReference.newNodeRef();
        AlfrescoDataSet snapshotBranch1 = dataSet.toBuilder()
                .addTransaction(txn -> {
                    txn.addNode(doc -> {
                        doc.nodeRef(nodeRef);
                        doc.type(Content.CONTENT);
                        doc.name("bar.txt");
                    });
                })
                .build();

        // creating a new branch from the same snapshot
        AlfrescoDataSet snapshotBranch2 = dataSet.toBuilder()
                .addTransaction(txn -> {
                    txn.addNode(doc -> {
                        doc.nodeRef(nodeRef);
                        doc.type(Content.CONTENT);
                        doc.name("branch2.txt");
                    });
                })
                .build();

        // the new node should NOT be present in the original snapshot
        assertThat(dataSet.getNodeView().getNode(nodeRef)).isNotPresent();

        // the new node SHOULD be present in the branch-1-snapshot
        assertThat(snapshotBranch1.getNodeView().getNode(nodeRef))
                .hasValueSatisfying(node -> assertThat(node)
                        .hasName("bar.txt")
                        .hasTxnId(12346L)
                        .hasNodeId(4322L));

        // the new node SHOULD be present in the branch-2-snapshot
        // and expecting the SAME txn-id and node-id
        assertThat(snapshotBranch2.getNodeView().getNode(nodeRef))
                .hasValueSatisfying(node -> assertThat(node)
                        .hasName("branch2.txt")
                        .hasTxnId(12346L)
                        .hasNodeId(4322L));
    }

    @Test
    void addRoot() {
        AlfrescoDataSet dataSetWithoutBootStrap = AlfrescoDataSet.builder()
                .addTransaction(txn -> {
                    txn.skipToNodeId(1000L);
                    txn.addRoot();
                }).build();

        assertThat(
                dataSetWithoutBootStrap.getNodeView().getNode(1000L)
                        .orElseThrow(NullPointerException::new).getParent())
                .isNull();

        AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco()
                .addTransaction(txn -> {
                    txn.skipToNodeId(1000L);
                    txn.addRoot();
                }).build();

        assertThat(dataSet.getNodeView().getNode(1000L).orElseThrow(NullPointerException::new).getParent())
                .isNull();
    }

}