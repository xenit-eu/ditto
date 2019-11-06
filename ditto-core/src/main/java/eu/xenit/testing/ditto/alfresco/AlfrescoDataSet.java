package eu.xenit.testing.ditto.alfresco;

import eu.xenit.testing.ditto.alfresco.DictionaryModel.SystemModel;
import eu.xenit.testing.ditto.alfresco.Transaction.TransactionBuilder;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;

public class AlfrescoDataSet {

    @Getter
    private final TransactionContainer transactions;

    @Getter
    private final NodeContainer nodeContainer;

    private final Map<String, Node> namedReferences;

    private AlfrescoDataSet(Builder builder) {
        this.transactions = new TransactionContainer();
        this.nodeContainer = new NodeContainer();
        this.namedReferences = new HashMap<>(builder.context.getNamedReferences());

        builder.transactions.forEach(this::processTransaction);
    }

    private void processTransaction(Transaction txn) {
        this.transactions.add(txn);

        txn.getUpdated().forEach(this.nodeContainer::add);
        txn.getDeleted().forEach(this.nodeContainer::delete);

    }

    public Node getNamedReference(String name) {
        return this.namedReferences.get(name);
    }

    public static Builder builder() {
        return builder(Instant.now());
    }

    /**
     * Allows to specify the instant the transaction log gets bootstrapped, to make all tests independent from the
     * current system clock
     */
    public static Builder builder(Instant bootstrapInstant) {
        return new Builder(bootstrapInstant);
    }

    public static AlfrescoDataSet empty() {
        return builder().build();
    }

    public static class Builder {

        private DefaultContext context;
        private LinkedList<Transaction> transactions = new LinkedList<>();

        private Builder(Instant bootstrapInstant) {
            context = new DefaultContext(bootstrapInstant);
        }

        public Builder addTransaction(Consumer<TransactionBuilder> callback) {
            TransactionBuilder txnBuilder = Transaction.builder(this.context);
            callback.accept(txnBuilder);
            this.transactions.add(txnBuilder.build());
            return this;
        }

        public Builder skipToTransaction(long newTxnSeqId) {
            this.context.skipToTransactionId(newTxnSeqId);
            return this;
        }

        public Builder setDefaultContentUrlProvider() {
            return this.setContentUrlProvider(null);
        }

        public Builder setContentUrlProvider(ContentUrlProvider provider) {
            this.context.setContentUrlProvider(provider);
            return this;
        }

        public Builder bootstrapAlfresco() {
            return this.skipToTransaction(6)
                    .addTransaction(txn -> txn
                            .skipToNodeId(12)
                            .addNode(node -> node
                                    .type(SystemModel.StoreRoot)
                            )
                            .addFolder("Company Home")
                            .addFolder("Space Templates")

                            .skipToNodeId(26)
                            .addFolder("User Homes")
                    );
        }

        public AlfrescoDataSet build() {
            return new AlfrescoDataSet(this);
        }
    }
}
