package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.model.Namespace;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

public interface BootstrapConfiguration {

    Instant getBootstrapInstant();
    Locale getDefaultLocale();
    Set<Namespace> getNamespaces();

    static BootstrapConfiguration withDefaults() {
        return new DefaultBootstrapConfiguration();
    }

    static BootstrapConfiguration withBootstrapInstant(Instant instant) {
        Objects.requireNonNull(instant, "Argument instant is required");
        return new DefaultBootstrapConfiguration().setBootstrapInstant(instant);
    }

    BootstrapConfiguration withNamespaces(Namespace ... namespaces);


    @Data
    @Accessors(chain = true)
    class DefaultBootstrapConfiguration implements BootstrapConfiguration {

        private Instant bootstrapInstant = Instant.now();
        private Set<Namespace> namespaces = new HashSet<>();
        private Locale defaultLocale = Locale.US;

        private DefaultBootstrapConfiguration()
        {

        }

        @Override
        public BootstrapConfiguration withNamespaces(Namespace... namespaces) {
            this.namespaces.addAll(Arrays.asList(namespaces));
            return this;
        }
    }

}
