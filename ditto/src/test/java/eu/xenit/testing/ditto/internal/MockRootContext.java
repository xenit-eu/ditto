package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.BootstrapConfiguration;

public class MockRootContext extends RootContext {

    public MockRootContext() {
        super(BootstrapConfiguration.withDefaults());
    }
}
