package eu.xenit.testing.ditto.api;

import java.time.Instant;
import java.util.Objects;
import lombok.Data;
import lombok.experimental.Accessors;

public interface BootstrapConfiguration {

    Instant getBootstrapInstant();

    static BootstrapConfiguration withDefaults() {
        return new DefaultBootstrapConfiguration();
    }

    static BootstrapConfiguration withBootstrapInstant(Instant instant) {
        Objects.requireNonNull(instant, "Argument instant is required");
        return new DefaultBootstrapConfiguration().setBootstrapInstant(instant);
    }

    @Data
    @Accessors(chain = true)
    class DefaultBootstrapConfiguration implements BootstrapConfiguration {

        private Instant bootstrapInstant = Instant.now();

        private DefaultBootstrapConfiguration()
        {

        }
    }

}
