package eu.xenit.testing.ditto.internal;

import static eu.xenit.testing.ditto.internal.DittoAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeAssert;
import eu.xenit.testing.ditto.api.NodeHolder;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.MLText;
import eu.xenit.testing.ditto.api.model.Namespace;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.ParentChildNodeCollection;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionContext;
import java.time.Instant;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DefaultNodeTest {

    @Test
    void nodeAndTxnId() {
        RootContext root = mock(RootContext.class);
        when(root.now()).thenReturn(Instant.now());
        when(root.nextTxnId()).thenReturn(7L);
        when(root.nextNodeId()).thenReturn(19L);
        TransactionContext txn = new TransactionContext(root);

        Node node = DefaultNode.builder(txn, null, null).build();

        assertThat(node)
                .hasTxnId(7)
                .hasNodeId(19);
    }

    @Test
    void parentDefaultsToCompanyHome() {
        NodeHolder node = new NodeHolder();

        AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco().addTransaction(txn -> {
            Node foo = txn.addNode(n -> n.name("foo.txt"));
            node.set(foo);
        }).build();

        Node companyHome = dataSet.getNodeView().getCompanyHome().orElseThrow(NoSuchElementException::new);
        assertThat(node.get()).hasParent(p -> p.isEqualTo(companyHome));
    }

    @Test
    void mlTextProperties() {
        RootContext root = mock(RootContext.class);
        when(root.now()).thenReturn(Instant.now());
        when(root.nextTxnId()).thenReturn(7L);
        TransactionContext txn = new TransactionContext(root);
        when(txn.defaultLocale()).thenReturn(Locale.ITALIAN);

        final String name = "My Node Name";
        final String title = "My Fantastic Title";

        Node node = DefaultNode.builder(txn, null, null)
                .property(Content.NAME, name)
                .property(Content.TITLE, MLText.create(Locale.ITALIAN, title))
                .build();

        assertThat(node.getProperties().get(Content.TITLE))
                .isPresent()
                .hasValueSatisfying(value -> assertThat(value)
                        .isExactlyInstanceOf(MLText.class)
                        .satisfies(val -> {
                            MLText mlText = (MLText) val;
                            assertThat(mlText.get(Locale.ITALIAN)).isEqualTo(title);
                            assertThat(mlText.get(Locale.CHINESE)).isNull();
                        }));
        assertThat(node.getProperties().getMLText(Content.TITLE, Locale.ITALIAN)).hasValue(title);
        assertThat(node.getProperties().getMLText(Content.TITLE, Locale.CHINA)).isNotPresent();
        assertThat(node.getProperties().get(Content.NAME)).hasValue(name);

    }

    @Test
    void parentAssocsSingle() {
        final Node[] parents = new Node[1];
        final String[] noderefs = new String[1];

        AlfrescoDataSet dataSet = AlfrescoDataSet
                .bootstrapAlfresco(Instant.now())
                .skipToTransaction(123L)
                .addTransaction(txn -> {
                    parents[0] = txn.addNode(doc -> {
                        doc.type(Content.FOLDER);
                        doc.name("foo");
                        doc.property("cm:description", "Folder description");
                    });
                })
                .skipToTransaction(456L)
                .addTransaction((txn -> {
                    txn.addNode(parents[0], doc -> {
                        doc.type(Content.CONTENT);
                        doc.name("bar.txt");
                        doc.content("bar");
                        doc.property("cm:description", "Document description");
                        noderefs[0] = doc.nodeRef().toString();
                    });
                }))
                .build();

        Optional<Node> node1 = dataSet.getNodeView().getNode(noderefs[0]);
        assertThat(node1).isPresent().hasValueSatisfying(value -> {
            assertThat(value).isInstanceOf(Node.class)
                    .satisfies(node -> {
                        ParentChildNodeCollection parentNodeCollection = node.getParentNodeCollection();
                        assertThat(parentNodeCollection.getAssociations().count()).isEqualTo(1);
                        assertThat(parentNodeCollection.getAssociations().collect(Collectors.toList())
                                .get(0).getParent()).isEqualTo(parents[0]);
                    });
        });
    }

    @Test
    void peerAssociation() {
        Namespace NAMESPACE = Namespace.createNamespace("myCustomSpace", "mcs");
        String localName = "myLocalName";
        QName myQName = QName.createQName(NAMESPACE, localName);
        final Node[] nodes = new Node[2];
        AlfrescoDataSet dataSet = AlfrescoDataSet
                .bootstrapAlfresco(Instant.now())
                .skipToTransaction(123L)
                .addTransaction(txn -> {
                    nodes[0] = txn.addNode(doc -> {
                        doc.type(Content.FOLDER);
                        doc.name("foo");
                        doc.property("cm:description", "Folder description");
                    });
                })
                .skipToTransaction(456L)
                .addTransaction((txn -> {
                    nodes[1] = txn.addNode(doc -> {
                        doc.type(Content.CONTENT);
                        doc.name("bar.txt");
                        doc.content("bar");
                        doc.property("cm:description", "Document description");
                        doc.sourceAssociation(nodes[0], myQName);
                    });
                }))
                .build();
        NodeView nodeView = dataSet.getNodeView();
        Optional<Node> node1 = nodeView.getNode(nodes[1].getNodeId());
        assertThat(node1.isPresent()).isTrue();
        assertThat(node1.get().getSourceAssociationCollection().getAssociations())
                .hasOnlyOneElementSatisfying(assoc -> {
                    assertThat(assoc.getSourceNode().getNodeRef()).isEqualTo(nodes[0].getNodeRef());
                    assertThat(assoc.getTargetNode().getNodeRef()).isEqualTo(nodes[1].getNodeRef());
                    assertThat(assoc.getSourceNode().getType()).isEqualTo(nodes[0].getType());
                    assertThat(assoc.getTargetNode().getType()).isEqualTo(nodes[1].getType());
                    assertThat(assoc.getAssocTypeQName()).isEqualTo(myQName);
                });
        Optional<Node> node0 = nodeView.getNode(nodes[0].getNodeId());
        assertThat(node0.isPresent()).isTrue();
        assertThat(node0.get().getTargetAssociationCollection().getAssociations())
                .hasOnlyOneElementSatisfying(assoc -> {
                    assertThat(assoc.getSourceNode().getNodeRef()).isEqualTo(nodes[0].getNodeRef());
                    assertThat(assoc.getTargetNode().getNodeRef()).isEqualTo(nodes[1].getNodeRef());
                    assertThat(assoc.getSourceNode().getType()).isEqualTo(nodes[0].getType());
                    assertThat(assoc.getTargetNode().getType()).isEqualTo(nodes[1].getType());
                    assertThat(assoc.getAssocTypeQName()).isEqualTo(myQName);
                });
    }


}