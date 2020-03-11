package eu.xenit.testing.ditto.internal.repository;

import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.record.RecordChain;
import eu.xenit.testing.ditto.internal.record.RecordLogEntry;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Cursor {

    @Getter(AccessLevel.PACKAGE)
    private final RecordLogEntry<Transaction> head;

    public RecordChain chain() {
//        return head.path().stream().map(RecordLogEntry::getId);
        return head.chain();
    }

}
