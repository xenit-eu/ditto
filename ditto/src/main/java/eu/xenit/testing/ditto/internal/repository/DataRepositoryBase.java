package eu.xenit.testing.ditto.internal.repository;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.internal.record.RecordChain;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.security.auth.kerberos.DelegationPermission;

public abstract class DataRepositoryBase {


    protected <TData> DataStorageOperation<TData> store(RecordChain chain, long recordId, TData data) {
        return this.store(chain, recordId, data, false);

    }

    protected <TData> DataStorageOperation<TData> store(RecordChain chain, long recordId, TData data, boolean deleted) {
        return new DataStorageOperation<>(chain, recordId, data, deleted);
    }

    protected <TKey, TData> Optional<TData> load(HashMap<TKey, RecordTuple<TData>> data, TKey key, RecordChain chain) {
        RecordTuple<TData> tuple = data.get(key);
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

//    protected <I> void storeWithIndex(HashMap<I, RecordTuple<TData>> store, RecordChain chain, RecordTuple<TData> tuple,
//            Function<RecordTuple<TData>, I> keyFunction) {
//
//        Objects.requireNonNull(store, "Argument 'store' is required");
//        Objects.requireNonNull(chain, "Argument 'chain' is required");
//        Objects.requireNonNull(tuple, "Argument 'tuple' is required");
//        Objects.requireNonNull(keyFunction, "Argument 'keyFunction' is required");
//
//        RecordTuple<TData> root = store.computeIfAbsent(
//                keyFunction.apply(tuple),
//                id -> new RecordTuple<>(0L, null, true));
//
//        RecordTuple<TData> parent = root.walk(chain.iterator());
//        parent.addChild(tuple);
//    }

    static class DataStorageOperation<TData> {

        private final RecordChain chain;
        private final long recordId;
        private final TData data;
        private final boolean deleted;

        private DataStorageOperation(RecordChain chain, long recordId, TData data, boolean deleted) {
            Objects.requireNonNull(chain, "Argument 'chain' is required");
            Objects.requireNonNull(data, "Argument 'data' is required");

            this.chain = chain;
            this.recordId = recordId;
            this.data = data;
            this.deleted = deleted;
        }

        public <I> DataStorageOperation<TData> withIndex(HashMap<I, RecordTuple<TData>> store,
                Function<RecordTuple<TData>, I> keyFunction) {

            Objects.requireNonNull(store, "Argument 'store' is required");
            Objects.requireNonNull(keyFunction, "Argument 'keyFunction' is required");

            RecordTuple<TData> tuple = newTuple();
            RecordTuple<TData> root = store.computeIfAbsent(
                    keyFunction.apply(tuple),
                    id -> new RecordTuple<>(0L, null, true));

            RecordTuple<TData> parent = root.walk(chain.iterator());
            parent.addChild(tuple);

            return this;
        }

        private RecordTuple<TData> newTuple() {
            return new RecordTuple<>(recordId, data, deleted);
        }
    }
}
