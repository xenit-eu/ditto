package eu.xenit.testing.ditto.api.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Data;

public class MLText implements Serializable {

    private final LinkedHashMap<Locale, String> data = new LinkedHashMap<>();

    private final Locale defaultLocale;

    public static MLText create(Locale locale, String value) {
        Objects.requireNonNull(locale, "Argument 'locale' is required");
        return new MLText(locale, value);
    }

    private MLText(Locale locale, String value) {
        this.data.put(locale, value);
        this.defaultLocale = locale;
    }

    public MLText put(Locale locale, String value) {
        this.data.put(locale, value);
        return this;
    }

    public String get(Locale local) {
        return this.data.get(local);
    }

    public String get() {
        return this.get(this.defaultLocale);
    }

    @Override
    public String toString() {
        return this.get();
    }

    public Stream<Entry<Locale, String>> stream() {
        return this.data.entrySet().stream();
    }

}
