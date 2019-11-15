package eu.xenit.testing.ditto.alfresco;


import eu.xenit.testing.ditto.alfresco.DictionaryModel.ContentModel;
import eu.xenit.testing.ditto.alfresco.DictionaryModel.SystemModel;
import eu.xenit.testing.ditto.alfresco.Node.NodeContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NodeInitializer {


    private final Set<String> auditableBlackListTypes = new HashSet<>(Collections.singletonList(SystemModel.STORE_ROOT));
    private final String DEFAULT_LOCALE = "en_US";

    public void accept(Node node, NodeContext context) {

        this.setDefaultSystemProperties(node);
        this.setDefaultContentModelProperties(node);
        this.setAuditableAspectAndProps(node, context);
        this.setContentData(node, context);
    }

    private void setDefaultSystemProperties(Node node) {
        // primary node-id is also present as a property
        node.getProperties().put(SystemModel.NODE_DBID, node.getNodeId());

        // noderef properties
        node.getProperties().putIfAbsent(SystemModel.STORE_PROTOCOL, node.getNodeRef().getStoreProtocol());
        node.getProperties().putIfAbsent(SystemModel.STORE_IDENTIFIER, node.getNodeRef().getStoreIdentifier());
        node.getProperties().putIfAbsent(SystemModel.NODE_UUID, node.getNodeRef().getUuid());

        node.getProperties().putIfAbsent(SystemModel.LOCALE, DEFAULT_LOCALE);
    }

    private void setDefaultContentModelProperties(Node node) {
        // If the name is missing, use the uuid
        node.getProperties().putIfAbsent(ContentModel.NAME, node.getNodeRef().getUuid());

        // title
        // description
    }

    /**
     * Initialize auditable aspects & properties for a node.
     * Given that dictionary-model type inheritance is not yet supported here,
     * we fall back to a blacklist of types where we should NOT inject auditable props
     *
     * @param node
     */
    private void setAuditableAspectAndProps(Node node, NodeContext context) {

        if (auditableBlackListTypes.contains(node.getType()))
        {
            return;
        }

        node.getAspects().add(ContentModel.AUDITABLE);

        node.getProperties().putIfAbsent(ContentModel.CREATOR, "System");
        node.getProperties().putIfAbsent(ContentModel.CREATED, context.getInstant().toString());
        node.getProperties().putIfAbsent(ContentModel.MODIFIER, "System");
        node.getProperties().putIfAbsent(ContentModel.MODIFIED, context.getInstant().toString());
    }

    private void setContentData(Node node, NodeContext context) {
        if (!node.isDocument()) {
            return;
        }

        ContentUrlProvider contentUrlProvider = context.getContentUrlProvider();
        String contentUrl = contentUrlProvider.createContentData(node, context);

        String contentData = String.format("contentUrl=%s|mimetype=%s|size=%s|encoding=%s|locale=%s|id=%s",
                contentUrl,
                node.getMimeType(),
                node.getSize(),
                "TODO-ENCODING",
                "TODO-LOCALE",
                "TODO-ID");

        node.getProperties().putIfAbsent(ContentModel.CONTENT, contentData);
    }

}

