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
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class DefaultNodeProperties implements NodeProperties {

    private final Map<QName, Serializable> properties = new LinkedHashMap<>();

    public DefaultNodeProperties() {
        this(Collections.emptyMap());
    }

    public DefaultNodeProperties(Map<QName, Serializable> properties) {
        this.properties.putAll(properties);
    }

    @Override
    public Optional<ContentData> getContentData() {
        return this.get(Content.CONTENT).map(ContentData.class::cast);
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
    public Optional<Serializable> get(QName key) {
        return Optional.ofNullable(this.properties.get(key));
    }

    public Serializable put(QName s, Serializable value) {
        return this.properties.put(s, value);
    }

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

    public Serializable putIfAbsent(QName key, Serializable value) {
        return this.properties.putIfAbsent(key, value);
    }

    @Override
    public Optional<String> getMLText(QName key, Locale locale) {
        return get(key).map(value -> {
            if (value instanceof MLText) {
                MLText mlValue = (MLText) value;
                return mlValue.get(locale);
            }
            String msg = String.format("Property '%s' is not of type d:mltext", get(key));
            throw new IllegalStateException(msg);
        });
    }

}
