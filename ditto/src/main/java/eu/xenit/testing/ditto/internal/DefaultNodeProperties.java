package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.ContentData;
import eu.xenit.testing.ditto.api.model.MLText;
import eu.xenit.testing.ditto.api.model.NodeProperties;
import eu.xenit.testing.ditto.api.model.QName;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class DefaultNodeProperties implements NodeProperties {

    private final Map<QName, Serializable> properties = new LinkedHashMap<>();
    private Locale defaultLocale;

    public DefaultNodeProperties() {
        this(Collections.emptyMap());
    }

    public DefaultNodeProperties(Map<QName, Serializable> properties) {
        this(Locale.ENGLISH, properties);
    }

    public DefaultNodeProperties(Locale defaultLocale, Map<QName, Serializable> properties) {
        this.defaultLocale = defaultLocale;
        this.properties.putAll(properties);
    }

    @Override
    public Optional<ContentData> getContentData() {
        return Optional.ofNullable((ContentData) this.get(Content.CONTENT));
    }

    @Override
    public int size() {
        return this.properties.size();
    }

    @Override
    public boolean isEmpty() {
        return this.properties.isEmpty();
    }

    @Override
    public boolean containsKey(QName key) {
        return this.properties.containsKey(key);
    }

    @Override
    public Serializable get(QName key) {
        return this.properties.get(key);
    }

    @Override
    public Serializable put(QName s, Serializable value) {
        return this.properties.put(s, value);
    }

    @Override
    public Serializable remove(QName key) {
        return this.properties.remove(key);
    }

    @Override
    public Set<QName> keySet() {
        return this.properties.keySet();
    }

    @Override
    public Collection<Serializable> values() {
        return this.properties.values();
    }

    @Override
    public Serializable getOrDefault(QName key, Serializable defaultValue) {
        return this.properties.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super QName, ? super Serializable> biConsumer) {
        this.properties.forEach(biConsumer);
    }

    @Override
    public Stream<Map.Entry<QName, Serializable>> stream() {
        return this.properties.entrySet().stream();
    }

    @Override
    public Serializable putIfAbsent(QName key, Serializable value) {
        return this.properties.putIfAbsent(key, value);
    }

    @Override
    public Locale defaultLocale() {
        return defaultLocale;
    }

    @Override
    public String getMLText(QName key, Locale locale) {
        Serializable mlTextValue = get(key);
        if (mlTextValue == null) {
            return null;
        }
        if (!(mlTextValue instanceof MLText)) {
            throw new IllegalStateException(String.format("Property '%s' is not of type d:mltext", mlTextValue));
        }
        return ((MLText) mlTextValue).get(locale);
    }

    @Override
    public MLText putMLText(QName s, Locale locale, String value) {
        Objects.requireNonNull(locale, "Argument 'locale' should not be null");
        if (this.properties.containsKey(s)) {
            Serializable existing = this.properties.get(s);
            if (!(existing instanceof MLText)) {
                throw new IllegalStateException(String.format("Property '%s' is not of type d:mltext", existing));
            }
            return (MLText) this.properties.put(s, ((MLText) existing).put(locale, value));
        }
        return (MLText) this.properties.put(s, MLText.create(locale, value));
    }

}
