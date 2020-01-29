package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.BootstrapConfiguration;
import eu.xenit.testing.ditto.api.DataSetBuilder;
import eu.xenit.testing.ditto.api.DataSetBuilderFactory;

public class DefaultDataSetBuilderFactory implements DataSetBuilderFactory {

    @Override
    public DefaultDataSetBuilder createBuilder(BootstrapConfiguration config) {
        return new DefaultDataSetBuilder(config);
    }
}
