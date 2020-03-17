package eu.xenit.testing.ditto.internal.record;

import java.util.stream.Stream;

public interface RecordChain extends Iterable<Long> {

    Stream<Long> stream();
    boolean contains(long id);
    int size();
}
