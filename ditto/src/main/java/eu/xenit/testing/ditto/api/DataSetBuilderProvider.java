package eu.xenit.testing.ditto.api;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class DataSetBuilderProvider {

    private Iterable<DataSetBuilderFactory> loader;

    private DataSetBuilderProvider() {
        this(ServiceLoader.load(DataSetBuilderFactory.class));
    }

    DataSetBuilderProvider(Iterable<DataSetBuilderFactory> loader) {
        Objects.requireNonNull(loader, "Argument loader is required");

        this.loader = loader;
    }

    private static DataSetBuilderProvider provider;
    static DataSetBuilderProvider getInstance() {
        if (provider == null) {
            provider = new DataSetBuilderProvider();
        }
        return provider;
    }

    /**
     * Returns a list of candidates from the {@link ServiceLoader}. The candidate-stream is sorted, the first item has
     * the highest priority.
     *
     * @return a sorted list of {@link DataSetBuilderFactory} candidates
     */
    Stream<DataSetBuilderFactory> candidates() {
        return StreamSupport.stream(this.loader.spliterator(), false).sorted();
    }

    /**
     * @return the @{code DataSetBuilder} with highest preu.xenit.testing.ditto.internal.DefaultDataSetBuilderFactoryiority
     * @throws @{code NoSuchElementException} when there is no implementation of @{link DataSetBuilder} found on the
     * classpath
     */
    DataSetBuilderFactory getFactory() {
        Optional<DataSetBuilderFactory> builder = this.candidates().findFirst();

        return builder.orElseThrow(() -> {
            String msg = String.format("No implementation for '%s' found.", DataSetBuilderFactory.class.getSimpleName());
            return new NoSuchElementException(msg);
        });
    }


}
