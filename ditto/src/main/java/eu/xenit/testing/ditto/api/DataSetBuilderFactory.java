package eu.xenit.testing.ditto.api;

public interface DataSetBuilderFactory extends Comparable<DataSetBuilderFactory> {

    DataSetBuilder createBuilder(BootstrapConfiguration config);

    /**
     * Defines the priority for a {@link DataSetBuilderFactory}, used when loaded from
     * {@link DataSetBuilderProvider} or any other {@link java.util.ServiceLoader}.
     *
     * Lower value means higher priority. Defaults to 0.
     *
     * @return the priority
     */
    default int getOrder() {
        return 0;
    }

    default int compareTo(DataSetBuilderFactory other) {
        return this.getOrder() - other.getOrder();
    }
}
