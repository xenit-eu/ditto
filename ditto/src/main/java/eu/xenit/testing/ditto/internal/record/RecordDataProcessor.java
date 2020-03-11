package eu.xenit.testing.ditto.internal.record;

@FunctionalInterface
public interface RecordDataProcessor<TAggregate> {

    void process(long recordId, RecordChain chain, TAggregate data);

}
