package eu.xenit.testing.ditto.internal.mvcc;

import java.util.Objects;
import java.util.UUID;
import lombok.Data;

@Data
public class RecordLogEntry<TAggregate> {

    private final RecordLogEntry<TAggregate> parent;
    private final TAggregate data;
    private final UUID id;

    RecordLogEntry() {
        this.parent = null;
        this.data = null;
        this.id = UUID.randomUUID();
    }

    public RecordLogEntry(RecordLogEntry<TAggregate> parent, TAggregate data/*, TAggregateId dataId*/) {
        Objects.requireNonNull(parent, "parent is required");
        Objects.requireNonNull(data, "data is required");
//        Objects.requireNonNull(dataId, "id is required");

        this.parent = parent;
        this.data = data;
//        this.id = dataId;
        this.id = UUID.randomUUID();
    }

//    public String getDataId() {
//        return this.data.getChangeId();
//    }
}
