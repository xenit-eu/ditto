package eu.xenit.testing.ditto.internal;

import static eu.xenit.testing.ditto.internal.DefaultParentChildNodeCollectionTest.node;
import static eu.xenit.testing.ditto.internal.DittoAssertions.assertThat;

import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionContext;
import org.junit.jupiter.api.Test;

public class NodeInitializerTests {

    private static final RootContext ctx = new MockRootContext();

    @Test
    void testBiDirectionalParentBinding() {
        RootContext ctx = new MockRootContext();
        Node parent = NodeTestUtil.node(ctx, null, null);
        Node child = NodeTestUtil.node(ctx, parent, Content.CONTAINS);

        assertThat(child)
                .hasParent(p -> p
                        .isEqualTo(parent)
                        .containsChildsOf(Content.CONTAINS, child));
    }


}
