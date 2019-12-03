package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.BuilderConfigurer;
import eu.xenit.testing.ditto.api.content.SwarmContentServiceCustomizer;
import eu.xenit.testing.ditto.internal.content.FileSystemContentUrlProvider;
import eu.xenit.testing.ditto.internal.content.SwarmContentUrlProvider;
import eu.xenit.testing.ditto.internal.content.SwarmContentServiceConfiguration;
import java.util.function.Consumer;

class DefaultBuilderConfigurer implements BuilderConfigurer {

    private final RootContext context;

    DefaultBuilderConfigurer(RootContext context) {

        this.context = context;
    }

    @Override
    public BuilderConfigurer resetDefaultContentUrlProvider() {
        this.context.setContentUrlProvider(null);
        return this;
    }

    @Override
    public BuilderConfigurer useFileSystemContentService() {
        this.context.setContentUrlProvider(new FileSystemContentUrlProvider());
        return this;
    }

    @Override
    public BuilderConfigurer useSwarmContentService(Consumer<SwarmContentServiceCustomizer> customizer) {
        SwarmContentServiceConfiguration config = new SwarmContentServiceConfiguration();
        customizer.accept(config);

        SwarmContentUrlProvider swarm = new SwarmContentUrlProvider(config.bucket());
        this.context.setContentUrlProvider(swarm);

        return this;
    }

}
