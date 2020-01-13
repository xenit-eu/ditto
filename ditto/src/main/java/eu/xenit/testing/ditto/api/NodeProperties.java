package eu.xenit.testing.ditto.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public interface NodeProperties {


    int size();
    boolean isEmpty();
    boolean containsKey(String key);

    Serializable get(String key);
    Serializable getOrDefault(String key, Serializable defaultValue);

    Serializable put(String s, Serializable value);
    Serializable putIfAbsent(String key, Serializable value);

    Serializable remove(String key);

    Set<String> keySet();
    Collection<Serializable> values();

    void forEach(BiConsumer<? super String, ? super Serializable> biConsumer);

    Optional<ContentData> getContentData();

}
