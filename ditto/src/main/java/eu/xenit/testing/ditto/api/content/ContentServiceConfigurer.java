package eu.xenit.testing.ditto.api.content;

import java.util.function.Consumer;

public interface ContentServiceConfigurer<T> {

    T resetDefaultContentUrlProvider();
    T useFileSystemContentService();
    T useSwarmContentService(Consumer<SwarmContentServiceCustomizer> customizer);

}
