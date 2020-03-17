package eu.xenit.testing.ditto.internal.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

public class DefaultRecordChain implements RecordChain {

    private final LinkedHashSet<Long> chain = new LinkedHashSet<>();

    // TODO can we somehow use a data structure that simply shares data with parent ?
    DefaultRecordChain(Stream<Long> parent, long self) {
        Stream.concat(parent, Stream.of(self)).forEach(chain::add);
    }

    DefaultRecordChain(RecordChain parent, long self) {
        this(parent.stream(), self);
    }

    @Override
    public boolean contains(long id) {
        return this.chain.contains(id);
    }

    @Override
    public int size() {
        return this.chain.size();
    }

    @Override
    public Iterator<Long> iterator() {
        return this.chain.iterator();
    }

    @Override
    public Stream<Long> stream() {
        return this.chain.stream();
    }
}
