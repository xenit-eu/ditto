package eu.xenit.testing.ditto.internal.record;

import java.util.Objects;
import java.util.stream.Stream;
import lombok.Data;

@Data
public class RecordLogEntry<TAggregate> {

    private final RecordLogEntry<TAggregate> parent;
    private final TAggregate data;
    //    private final UUID id;
    private final long id;

    private RecordChain _chainCache = null;

    // TODO don't actually need a full reference to the log, only the id-generator ?
    private final RecordLog<TAggregate> log;

    RecordLogEntry(RecordLog<TAggregate> log) {
        Objects.requireNonNull(log, "Argument 'log' can't be null");

        this.log = log;

        this.parent = null;
        this.data = null;
//        this.id = UUID.randomUUID();
        this.id = 0L;

        this._chainCache = new DefaultRecordChain(Stream.empty(), this.id);
    }

    //    public RecordLogEntry(RecordLogEntry<TAggregate> parent, TAggregate data/*, TAggregateId dataId*/) {
    public RecordLogEntry(RecordLogEntry<TAggregate> parent, TAggregate data) {
        Objects.requireNonNull(parent, "parent is required");
        Objects.requireNonNull(data, "data is required");

        this.parent = parent;
        this.data = data;
        this.log = parent.getLog();
        this.id = this.log.nextId();

        this._chainCache = new DefaultRecordChain(this.parent.chain(), this.id);
    }

    public RecordLogEntry(RecordLogEntry<TAggregate> parent, TAggregate data,
            RecordDataProcessor<TAggregate> callback) {
        this(parent, data);

        callback.process(this.id, parent.chain(), data);
    }

    /**
     * Stream of record-ids from root to current entry
     * This is actually reversing the linked list + hashset
     * - this class is a single linked list node, pointing to it's parent
     * - we need a linked-list data structure, pointing from the root to here AND O(1) contains check
     */
    public RecordChain chain() {
        return this._chainCache;
    }
}
