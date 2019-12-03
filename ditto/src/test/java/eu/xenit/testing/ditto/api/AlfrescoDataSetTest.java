package eu.xenit.testing.ditto.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class AlfrescoDataSetTest {

    @Test
    void basicIntegrationTest() {
        AlfrescoDataSet dataSet = AlfrescoDataSet.builder(Instant.now())
                .bootstrapAlfresco()
                .skipToTransaction(12345L)
                .addTransaction(txn -> {
                    txn.addDocument("foo.txt");
                })
                .build();

        assertThat(dataSet).isNotNull();

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

}