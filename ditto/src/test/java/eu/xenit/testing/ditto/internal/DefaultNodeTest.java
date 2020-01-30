package eu.xenit.testing.ditto.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.MLText;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.internal.DefaultTransaction.TransactionContext;
import java.time.Instant;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class DefaultNodeTest {

    @Test
    void txnId() {
        RootContext root = mock(RootContext.class);
        when(root.now()).thenReturn(Instant.now());
        when(root.nextTxnId()).thenReturn(7L);
        TransactionContext txn = new TransactionContext(root);

        Node node = DefaultNode.builder(txn, null, null).build();

        assertThat(node.getTxnId()).isEqualTo(7L);
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

}