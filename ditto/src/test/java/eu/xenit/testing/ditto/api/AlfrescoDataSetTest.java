package eu.xenit.testing.ditto.api;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.model.NodeReference;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class AlfrescoDataSetTest {

    private AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco()
            .skipToTransaction(12345L)
            .addTransaction(txn -> {
                txn.addDocument(null, doc -> {
                    doc.name("foo.txt");
                    doc.content("foobar");
                    doc.property("cm:description", "Test description");
                    NODEREF_FOO_TXT = doc.nodeRef();
                });
            })
            .build();

    private NodeReference NODEREF_FOO_TXT;

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

}