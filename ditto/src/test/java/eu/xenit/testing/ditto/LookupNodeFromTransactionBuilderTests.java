package eu.xenit.testing.ditto;

import static eu.xenit.testing.ditto.internal.DittoAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LookupNodeFromTransactionBuilderTests {

    private final NodeReference NODEREF_FOLDER = NodeReference.newNodeRef();

    private AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco()
            .configure(config -> {
                config.skipToTxnId(12345L);
                config.skipToNodeId(4321L);
            })
            .addTransaction(txn -> {
                txn.addFolder(txn.getCompanyHome().orElseThrow(NoSuchElementException::new), folder -> {
                    folder.name("my folder");
                    folder.nodeRef(NODEREF_FOLDER);
                });
            })
            .build();

    @Test
    void checkGetNodeInsideTxnBuilder() {

        AlfrescoDataSet snapshot = dataSet.toBuilder()
                .addTransaction(txn -> {
                    Optional<Node> nodeByNodeRef = txn.getNode(NODEREF_FOLDER);

                    assertThat(nodeByNodeRef).isPresent()

                            // check the properties match
                            .hasValueSatisfying(n -> assertThat(n).hasNodeId(4321L).hasName("my folder"))

                            // lookup the same node using nodeId
                            .isEqualTo(txn.getNode(4321L));

                    // add a sub-folder
                    Node subFolder = txn.addFolder(nodeByNodeRef.get(), subfolder -> {
                        subfolder.name("subfolder");
                        subfolder.uuid("ce410b9c-895f-11ea-97ae-bb5813fd442b");
                    });

                    // lookup the subfolder in the same txn as it is created
                    assertThat(txn.getNode(NodeReference.workspaceSpacesStore("ce410b9c-895f-11ea-97ae-bb5813fd442b")))
                            .hasValue(subFolder);

                    // and add another document
                    txn.addDocument(subFolder, doc -> {
                        doc.name("file.txt");
                        doc.uuid("851af42c-8960-11ea-a34b-d35f3f6a5a1d");
                    });
                })
                .addTransaction(txn -> {
                    // and lookup the document again

                    NodeReference nodeRef = NodeReference.workspaceSpacesStore("851af42c-8960-11ea-a34b-d35f3f6a5a1d");
                    assertThat(txn.getNode(nodeRef))
                            .hasValueSatisfying(n -> assertThat(n).hasName("file.txt"));
                })
                .build();

        // now check the snapshot
        Optional<Node> node = snapshot.getNodeView()
                .getNode(NodeReference.workspaceSpacesStore("851af42c-8960-11ea-a34b-d35f3f6a5a1d"));

        assertThat(node).hasValueSatisfying(n -> assertThat(n).hasName("file.txt"));


    }
}