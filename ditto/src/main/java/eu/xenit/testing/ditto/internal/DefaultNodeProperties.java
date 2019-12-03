package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.NodeProperties;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.internal.content.ContentDataParser;
import eu.xenit.testing.ditto.internal.content.ContentDataParser.ContentDataField;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public class DefaultNodeProperties implements NodeProperties {

    private final Map<String, Serializable> properties = new LinkedHashMap<>();

    public DefaultNodeProperties() {

    }

    public DefaultNodeProperties(Map<String, Serializable> properties) {
        this.properties.putAll(properties);
    }

    @Override
    public Optional<String> getContentData() {
        return Optional.ofNullable((String) this.get(Content.CONTENT));
    }

    @Override
    public Optional<String> getContentUrl() {
        return this.getContentData()
                .map(data -> ContentDataParser.extractField(data, ContentDataField.CONTENT_URL));
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
    public boolean containsKey(String key) {
        return this.properties.containsKey(key);
    }

    @Override
    public Serializable get(String key) {
        return this.properties.get(key);
    }

    @Override
    public Serializable put(String s, Serializable value) {
        return this.properties.put(s, value);
    }

    @Override
    public Serializable remove(String key) {
        return this.properties.remove(key);
    }

    @Override
    public Set<String> keySet() {
        return this.properties.keySet();
    }

    @Override
    public Collection<Serializable> values() {
        return this.properties.values();
    }

    @Override
    public Serializable getOrDefault(String key, Serializable defaultValue) {
        return this.properties.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Serializable> biConsumer) {
        this.properties.forEach(biConsumer);
    }

    @Override
    public Serializable putIfAbsent(String key, Serializable value) {
        return this.properties.putIfAbsent(key, value);
    }
}
