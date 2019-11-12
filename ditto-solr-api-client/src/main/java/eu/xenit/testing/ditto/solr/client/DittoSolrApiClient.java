package eu.xenit.testing.ditto.solr.client;

import eu.xenit.testing.ditto.alfresco.AlfrescoDataSet;
import eu.xenit.testing.ditto.alfresco.TransactionContainer;
import eu.xenit.testing.ditto.alfresco.TransactionFilter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.repo.index.shard.ShardState;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.client.Acl;
import org.alfresco.solr.client.AclChangeSet;
import org.alfresco.solr.client.AclChangeSets;
import org.alfresco.solr.client.AclReaders;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.client.AlfrescoModelDiff;
import org.alfresco.solr.client.GetNodesParameters;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.NodeMetaData;
import org.alfresco.solr.client.NodeMetaDataParameters;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.client.Transactions;
import org.alfresco.solr.client.TransactionsAccessor;
import org.json.JSONException;


public class DittoSolrApiClient extends SOLRAPIClient {

    private final AlfrescoDataSet dataSet;

    public DittoSolrApiClient(AlfrescoDataSet dataSet) {
        super(null, null, null);

        Objects.requireNonNull(dataSet, "Parameter 'dataSet' is required");
        this.dataSet = dataSet;
    }

    @Override
    public AclChangeSets getAclChangeSets(Long fromCommitTime, Long minAclChangeSetId, Long toCommitTime,
            Long maxAclChangeSetId, int maxResults) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Acl> getAcls(List<AclChangeSet> aclChangeSets, Long minAclId, int maxResults) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AclReaders> getAclReaders(List<Acl> acls) {
        throw new UnsupportedOperationException();
    }

    // https://github.com/Alfresco/alfresco-repository/blob/fb4c61c739b21db290e73d15f9056457bf6391de/src/main/resources/alfresco/ibatis/org.alfresco.repo.domain.dialect.Dialect/solr-common-SqlMap.xml#L89
    @Override
    public Transactions getTransactions(Long fromCommitTime, Long minTxnId, Long toCommitTime, Long maxTxnId,
            int maxResults) throws JSONException {

        TransactionContainer txnLog = this.dataSet.getTransactions();

        List<Transaction> transactions = txnLog.stream()
                .filter(TransactionFilter.fromCommitTime(fromCommitTime))
                .filter(TransactionFilter.minTxnId(minTxnId))
                .filter(TransactionFilter.toCommitTime(toCommitTime))
                .filter(TransactionFilter.maxTxnId(maxTxnId))
                .limit(maxResults)

                .map(txn -> {
                    Transaction solrTxn = new Transaction();
                    solrTxn.setId(txn.getId());
                    solrTxn.setCommitTimeMs(txn.getCommitTimeMs());
                    solrTxn.setUpdates(txn.getUpdated().size());
                    solrTxn.setDeletes(txn.getDeleted().size());
                    return solrTxn;
                })

                .collect(Collectors.toList());

        return new TransactionsAccessor(transactions, txnLog.getLastTxnId(), txnLog.getLastCommitTimeMs());

    }

    @Override
    public Transactions getTransactions(Long fromCommitTime, Long minTxnId, Long toCommitTime, Long maxTxnId,
            int maxResults, ShardState shardState) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Node> getNodes(GetNodesParameters parameters, int maxResults) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NodeMetaData> getNodesMetaData(NodeMetaDataParameters params, int maxResults) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetTextContentResponse getTextContent(Long nodeId, QName propertyQName, Long modifiedSince) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AlfrescoModel getModel(String coreName, QName modelName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AlfrescoModelDiff> getModelsDiff(String coreName, List<AlfrescoModel> currentModels) {
        throw new UnsupportedOperationException();
    }


}
