package eu.xenit.testing.ditto.api.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public interface NodeProperties {


    int size();
    boolean isEmpty();
    boolean containsKey(QName key);

    Serializable get(QName key);
    Serializable getOrDefault(QName key, Serializable defaultValue);

    Set<QName> keySet();
    Collection<Serializable> values();

    void forEach(BiConsumer<? super QName, ? super Serializable> biConsumer);
    Stream<Map.Entry<QName, Serializable>> stream();

    Optional<ContentData> getContentData();

    String getMLText(QName key, Locale locale);


    class Property {
        QName name;
        Serializable value;
    }
}
