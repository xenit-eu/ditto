package eu.xenit.testing.ditto.api.model;

import java.util.HashMap;
import java.util.Locale;

public class MLText extends HashMap<Locale, String> {

    private MLText(Locale locale, String value) {
        super();
        super.put(locale, value);
    }

    public MLText addValue(Locale locale, String value) {
        put(locale, value);
        return this;
    }

    public String getValue(Locale locale) {
        return get(locale);
    }

    public static MLText create(Locale locale, String value) {
        return new MLText(locale, value);
    }
}
