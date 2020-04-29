package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.NodeCustomizer;
import eu.xenit.testing.ditto.api.TransactionCustomizer;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.DefaultNode.NodeBuilder;
import eu.xenit.testing.ditto.internal.DefaultTransaction.DefaultTransactionBuilder;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionContext;
import java.util.function.Consumer;

public class NodeTestUtil {

    public static Transaction txn(RootContext ctx, Consumer<TransactionCustomizer> callback) {
        DefaultTransactionBuilder builder = DefaultTransaction.builder(ctx, null);
        callback.accept(builder);
        return builder.build();
    }

    public static DefaultNode node(RootContext ctx, Node parent, QName assocQName) {
        return node(ctx, parent, assocQName, (builder) -> { });
    }

    public static DefaultNode node(RootContext ctx, Node parent, QName assocQName, Consumer<NodeCustomizer> callback) {
        TransactionContext txn = new TransactionContext(ctx);

        NodeBuilder builder = DefaultNode.builder(txn, parent, assocQName);
        callback.accept(builder);

        return builder.build();
    }

}
