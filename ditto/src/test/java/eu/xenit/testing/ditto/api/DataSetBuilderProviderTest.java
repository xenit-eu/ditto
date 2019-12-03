package eu.xenit.testing.ditto.api;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.internal.DefaultDataSetBuilderFactory;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class DataSetBuilderProviderTest {

    @Test
    void getDefaultCandidates() {
        Stream<DataSetBuilderFactory> candidates = DataSetBuilderProvider.getInstance().candidates();

        assertThat(candidates).hasAtLeastOneElementOfType(DefaultDataSetBuilderFactory.class);
    }

    @Test
    void testCandidateSorting() {
        DataSetBuilderFactory factory_Zero = new TestOrderedBuilderFactory(0);
        DataSetBuilderFactory factory_Plus_1 = new TestOrderedBuilderFactory(1);
        DataSetBuilderFactory factory_Minus_1 = new TestOrderedBuilderFactory(-1);
        DataSetBuilderFactory factory_Plus_100 = new TestOrderedBuilderFactory(100);
        DataSetBuilderFactory factory_Minus_100 = new TestOrderedBuilderFactory(-100);

        DataSetBuilderProvider provider = new DataSetBuilderProvider(Arrays.asList(
                factory_Zero,
                factory_Plus_1,
                factory_Minus_1,
                factory_Plus_100,
                factory_Minus_100
        ));

        assertThat(provider.candidates())
                .containsExactly(
                        factory_Minus_100, factory_Minus_1,
                        factory_Zero,
                        factory_Plus_1, factory_Plus_100);
    }

    private class TestOrderedBuilderFactory implements DataSetBuilderFactory {

        private final int order;

        private TestOrderedBuilderFactory(int order) {

            this.order = order;
        }

        @Override
        public DataSetBuilder createBuilder(BootstrapConfiguration config) {
            return null;
        }

        @Override
        public int getOrder() {
            return this.order;
        }

        @Override
        public String toString() {
            return String.format("[%s order:%d]", this.getClass().getSimpleName(), this.getOrder());
        }
    }
}