package eu.xenit.testing.ditto.internal.repository;

import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.internal.record.RecordDataProcessor;

@FunctionalInterface
public interface TransactionProcessor extends RecordDataProcessor<Transaction> {

}
