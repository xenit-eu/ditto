package eu.xenit.testing.ditto.internal;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.BootstrapConfiguration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class RootContextTest {

    @Test
    public void testInstant_truncatedToMillis() {
        final String dateString = "2020-02-03T09:24:50.123";

        Instant instant = Instant.parse(dateString + "Z").plusNanos(456789L);
        assertThat(instant.toString()).isEqualTo(dateString + "456789Z");

        RootContext rootContext = new RootContext(BootstrapConfiguration.withBootstrapInstant(instant));
        assertThat(rootContext.now().toString()).isEqualTo(dateString + "Z");
    }

}