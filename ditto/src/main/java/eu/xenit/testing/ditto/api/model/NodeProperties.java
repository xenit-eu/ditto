package eu.xenit.testing.ditto.api.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public interface NodeProperties {


    int size();
    boolean isEmpty();
    boolean containsKey(QName key);

    Serializable get(QName key);
    Serializable getOrDefault(QName key, Serializable defaultValue);

    Serializable put(QName s, Serializable value);
    Serializable putIfAbsent(QName key, Serializable value);

    Serializable remove(QName key);

    Set<QName> keySet();
    Collection<Serializable> values();

    void forEach(BiConsumer<? super QName, ? super Serializable> biConsumer);

    Optional<ContentData> getContentData();

}
