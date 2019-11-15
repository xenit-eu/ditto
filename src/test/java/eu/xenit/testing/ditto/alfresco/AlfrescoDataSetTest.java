package eu.xenit.testing.ditto.alfresco;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class AlfrescoDataSetTest {

    @Test
    public void basicIntegrationTest() {
        AlfrescoDataSet dataSet = AlfrescoDataSet.builder(Instant.now())
                .bootstrapAlfresco()
                .addTransaction(txn -> {
                    txn.addDocument("foo.txt");
                })
                .build();

        assertThat(dataSet)
                .isNotNull()
                .satisfies(actual -> {
                    assertThat(actual.getNodeContainer().stream())
                            .filteredOn(node -> node.getName().equalsIgnoreCase("foo.txt"))
                            .hasSize(1);
                });

    }
}