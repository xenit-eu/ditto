package eu.xenit.testing.ditto.api.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Namespace {

    @Getter
    private final String namespace;

    @Getter
    @EqualsAndHashCode.Exclude
    private final Set<String> prefixes;

    private Namespace(String namespace, Collection<String> prefixes)
    {
        this.namespace = namespace != null ? namespace : "";
        this.prefixes = new HashSet<>(prefixes);
    }

    public static Namespace createNamespace(String namespace, String ... prefix) {
        return new Namespace(namespace, Arrays.asList(prefix));
    }

    public Optional<String> getPrimaryPrefix() {
        return this.prefixes.stream().findFirst();
    }



}
