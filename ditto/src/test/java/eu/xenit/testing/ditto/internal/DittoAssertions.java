package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.NodeAssert;
import eu.xenit.testing.ditto.api.TransactionAssert;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.Transaction;

public class DittoAssertions {

    public static DefaultDataSetBuilderAssert assertThat(DefaultDataSetBuilder builder) {
        return new DefaultDataSetBuilderAssert(builder);
    }

    public static TransactionAssert assertThat(Transaction transaction) {
        return new TransactionAssert(transaction);
    }

    public static NodeAssert assertThat(Node node) {
        return new NodeAssert(node);
    }

}
