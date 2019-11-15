package eu.xenit.testing.ditto.alfresco;

import eu.xenit.testing.ditto.alfresco.DictionaryModel.ContentModel;
import eu.xenit.testing.ditto.alfresco.internal.ContentDataParser;
import eu.xenit.testing.ditto.alfresco.internal.ContentDataParser.ContentDataField;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public class NodeProperties {

    private final Map<String, Serializable> properties = new LinkedHashMap<>();

    public NodeProperties() {
    }

    public Optional<String> getContentData() {
        return Optional.ofNullable((String) this.get(ContentModel.CONTENT));
    }

    public Optional<String> getContentUrl() {
        return this.getContentData()
                .map(data -> ContentDataParser.extractField(data, ContentDataField.CONTENT_URL));
    }

    public NodeProperties(Map<String, Serializable> properties) {
        this.properties.putAll(properties);
    }

    public int size() {
        return this.properties.size();
    }

    public boolean isEmpty() {
        return this.properties.isEmpty();
    }

    public boolean containsKey(String key) {
        return this.properties.containsKey(key);
    }

    public Serializable get(String key) {
        return this.properties.get(key);
    }

    public Serializable put(String s, Serializable value) {
        return this.properties.put(s, value);
    }

    public Serializable remove(String key) {
        return this.properties.remove(key);
    }

    public Set<String> keySet() {
        return this.properties.keySet();
    }

    public Collection<Serializable> values() {
        return this.properties.values();
    }

    public Serializable getOrDefault(String key, Serializable defaultValue) {
        return this.properties.getOrDefault(key, defaultValue);
    }

    public void forEach(BiConsumer<? super String, ? super Serializable> biConsumer) {
        this.properties.forEach(biConsumer);
    }

    public Serializable putIfAbsent(String key, Serializable value) {
        return this.properties.putIfAbsent(key, value);
    }
}
