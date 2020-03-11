package eu.xenit.testing.ditto.internal.repository;

import eu.xenit.testing.ditto.internal.record.RecordChain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
class RecordTuple<TData> {

    final long recordId;
    final TData data;
    boolean deleted;

    private final List<RecordTuple<TData>> children = new ArrayList<>();

    RecordTuple<TData> walk(RecordChain chain) {
        return this.walk(chain.iterator());
    }

    RecordTuple<TData> walk(Iterator<Long> path) {

        // first check we already have not arrived at the destinations
        if (!path.hasNext()) {
            return this;
        }

        // get the child-record-id we are looking for
        Long childId = path.next();

        return children.stream()
                // find a child of this record with matching record id
                .filter(child -> child.recordId == childId).findFirst()

                // or else this record is the good parent
                .orElse(this)

                // and continue walking the path till we reach the end
                .walk(path);
    }

    void addChild(RecordTuple<TData> child) {
        this.children.add(child);
    }
}
