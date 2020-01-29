package eu.xenit.testing.ditto.internal;

import static eu.xenit.testing.ditto.internal.DittoAssertions.assertThat;

import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionContext;
import org.junit.jupiter.api.Test;

public class NodeInitializerTests {

    private static RootContext ctx = new MockRootContext();

    @Test
    void testBiDirectionalParentBinding() {
        Node parent = node(null, null);
        DefaultNode child = node(parent, Content.CONTAINS);

        assertThat(child)
                .hasParent(p -> p
                        .isEqualTo(parent)
                        .containsChildsOf(Content.CONTAINS, child));
    }

    public static DefaultNode node(Node parent, QName assocQName) {

        TransactionContext txn = new TransactionContext(ctx);
        return DefaultNode.builder(txn, parent, assocQName).build();
    }
}
