package eu.xenit.testing.ditto.internal.repository;

import eu.xenit.testing.ditto.internal.record.RecordChain;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class DataRepositoryBase {


    protected <T> DataStorageOperation<T> store(RecordChain chain, long recordId, T data) {
        return this.store(chain, recordId, data, false);

    }

    protected <T> DataStorageOperation<T> store(RecordChain chain, long recordId, T data, boolean deleted) {
        return new DataStorageOperation<>(chain, recordId, data, deleted);
    }

    protected <K, T> Optional<T> load(HashMap<K, RecordTuple<T>> data, K key, RecordChain chain) {
        RecordTuple<T> tuple = data.get(key);
        if (tuple == null) {
            return Optional.empty();
        }

        tuple = tuple.walk(chain);

        if (tuple.deleted) {
            return Optional.empty();
        }

        assert tuple.data != null;
        return Optional.of(tuple.data);
    }

    static class DataStorageOperation<T> {

        private final RecordChain chain;
        private final long recordId;
        private final T data;
        private final boolean deleted;

        private DataStorageOperation(RecordChain chain, long recordId, T data, boolean deleted) {
            Objects.requireNonNull(chain, "Argument 'chain' is required");
            Objects.requireNonNull(data, "Argument 'data' is required");

            this.chain = chain;
            this.recordId = recordId;
            this.data = data;
            this.deleted = deleted;
        }

        public <I> DataStorageOperation<T> withIndex(HashMap<I, RecordTuple<T>> store,
                Function<RecordTuple<T>, I> keyFunction) {

            Objects.requireNonNull(store, "Argument 'store' is required");
            Objects.requireNonNull(keyFunction, "Argument 'keyFunction' is required");

            RecordTuple<T> tuple = newTuple();
            RecordTuple<T> root = store.computeIfAbsent(
                    keyFunction.apply(tuple),
                    id -> new RecordTuple<>(0L, null, true));

            RecordTuple<T> parent = root.walk(chain.iterator());
            parent.addChild(tuple);

            return this;
        }

        private RecordTuple<T> newTuple() {
            return new RecordTuple<>(recordId, data, deleted);
        }
    }
}
