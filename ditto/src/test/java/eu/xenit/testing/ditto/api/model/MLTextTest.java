package eu.xenit.testing.ditto.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class MLTextTest {

    @Test
    void testToString() {
        MLText foo = MLText.create(Locale.getDefault(), "foo");
        foo.put(Locale.FRENCH, "bar");

        assertThat(foo.toString()).isEqualTo("foo");
    }
}