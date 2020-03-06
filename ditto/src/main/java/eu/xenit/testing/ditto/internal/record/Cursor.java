package eu.xenit.testing.ditto.internal.record;

import lombok.Data;

@Data
public class Cursor<TAggregate> {

    public final RecordLog<TAggregate> log;
    public final RecordLogEntry<TAggregate> head;

}
