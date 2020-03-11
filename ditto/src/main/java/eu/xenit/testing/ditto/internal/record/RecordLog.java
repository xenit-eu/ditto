package eu.xenit.testing.ditto.internal.record;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import lombok.Data;
import lombok.Getter;

public class RecordLog<TAggregate> {

    @Getter
    private final RecordLogEntry<TAggregate> root;

    private final AtomicLong recordSeq = new AtomicLong(0);


    public RecordLog() {
        this.root = new RecordLogEntry<>(this);
    }

    long nextId() {
        return this.recordSeq.incrementAndGet();
    }

    /**
     * Processes the stream, starting from an empty state and returns the head.
     *
     * This head captures the last entry of the data stream and has a pointer to the previous entry. You can this of
     * this like a series of version control commits, starting from scratch.
     *
     * @param stream the stream of data to capture in the log
     * @param callback is a callback which should process the contents of this element
     * @return the head entry
     */
    public RecordLogEntry<TAggregate> process(Stream<TAggregate> stream, RecordDataProcessor<TAggregate> callback) {
        return this.process(this.root, stream, callback);
    }

    /**
     * Process a single transaction
     */
    public RecordLogEntry<TAggregate> process(TAggregate element, RecordDataProcessor<TAggregate> callback) {
        return this.process(Stream.of(element), callback);
    }

    /**
     * Processes the stream on top of the given parent and returns the new head of the log.
     *
     * This head captures the last entry of the data stream and has a pointer to the previous entry. You can this of
     * this like a series of version control commits, starting from a given commit.
     *
     * @param parent the log entry where to apply the data stream on top of
     * @param stream the stream of data to capture in the log
     * @return the head entry
     */
    public RecordLogEntry<TAggregate> process(RecordLogEntry<TAggregate> parent, Stream<TAggregate> stream,
            RecordDataProcessor<TAggregate> callback) {
        Objects.requireNonNull(parent, "parent is required");
        Objects.requireNonNull(stream, "stream is required");

        return stream.reduce(parent, (previous, element) -> new RecordLogEntry<>(previous, element, callback),
                PARALLEL_STREAMS_NOT_SUPPORTED);
    }

    private final BinaryOperator<RecordLogEntry<TAggregate>> PARALLEL_STREAMS_NOT_SUPPORTED = (l1, l2) -> {
        throw new UnsupportedOperationException("parallel streams not supported");
    };

}
