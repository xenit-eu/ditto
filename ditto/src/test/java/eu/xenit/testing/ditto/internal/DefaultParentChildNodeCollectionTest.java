package eu.xenit.testing.ditto.internal;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.ParentChildNodeCollection;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionContext;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class DefaultParentChildNodeCollectionTest {

    private static final RootContext ctx = new MockRootContext();

    @Test
    void addAssociation() {

        Node parent = node(null, null);
        Node child1 = node(parent, Content.CONTAINS);
        Node child2 = node(parent, Content.CONTAINS);
        Node child3 = node(parent, Content.CONTAINS);

        ParentChildNodeCollection collection = new DefaultParentChildNodeCollection(parent, Collections.emptyList());

        collection.addChild(child1, Content.CONTAINS);
        collection.addAssociation(new DefaultParentChildAssoc(parent, Content.CONTAINS, child2, true));
        collection.addAssociation(new DefaultParentChildAssoc(parent, Content.CONTAINS, child3, true));

        // add some duplicates
        collection.addAssociation(new DefaultParentChildAssoc(parent, Content.CONTAINS, child1, true));
        collection.addChild(child2, Content.CONTAINS);

        assertThat(collection.getChilds(Content.CONTAINS))
                .containsExactly(
                        child1, child2, child3
                );
    }

    public static Node node(Node parent, QName assocQName) {

        TransactionContext txn = new TransactionContext(ctx);
        return DefaultNode.builder(txn, parent, assocQName).build();
    }

}