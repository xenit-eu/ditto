package eu.xenit.testing.ditto.internal;


import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.internal.DefaultNode.NodeContext;
import eu.xenit.testing.ditto.internal.content.ContentUrlProviderSpi;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NodeInitializer {


    private final Set<String> auditableBlackListTypes = new HashSet<>(Collections.singletonList(System.STORE_ROOT));
    private final String DEFAULT_LOCALE = "en_US";

    public void accept(DefaultNode node, NodeContext context) {

        this.setDefaultSystemProperties(node);
        this.setDefaultContentModelProperties(node);
        this.setAuditableAspectAndProps(node, context);
        this.setContentData(node, context);
    }

    private void setDefaultSystemProperties(DefaultNode node) {
        // primary node-id is also present as a property
        node.getProperties().put(System.NODE_DBID, node.getNodeId());

        // noderef properties
        node.getProperties().putIfAbsent(System.STORE_PROTOCOL, node.getNodeRef().getStoreProtocol());
        node.getProperties().putIfAbsent(System.STORE_IDENTIFIER, node.getNodeRef().getStoreIdentifier());
        node.getProperties().putIfAbsent(System.NODE_UUID, node.getNodeRef().getUuid());

        node.getProperties().putIfAbsent(System.LOCALE, DEFAULT_LOCALE);
    }

    private void setDefaultContentModelProperties(DefaultNode node) {
        // If the name is missing, use the uuid
        node.getProperties().putIfAbsent(Content.NAME, node.getNodeRef().getUuid());

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
    private void setAuditableAspectAndProps(DefaultNode node, NodeContext context) {

        if (auditableBlackListTypes.contains(node.getType()))
        {
            return;
        }

        node.getAspects().add(Content.AUDITABLE);

        node.getProperties().putIfAbsent(Content.CREATOR, "System");
        node.getProperties().putIfAbsent(Content.CREATED, context.getInstant().toString());
        node.getProperties().putIfAbsent(Content.MODIFIER, "System");
        node.getProperties().putIfAbsent(Content.MODIFIED, context.getInstant().toString());
    }

    private void setContentData(DefaultNode node, NodeContext context) {
        if (!node.isDocument()) {
            return;
        }

        ContentUrlProviderSpi contentUrlProvider = context.getContentUrlProvider();
        String contentUrl = contentUrlProvider.createContentData(node, context);

        String contentData = String.format("contentUrl=%s|mimetype=%s|size=%s|encoding=%s|locale=%s|id=%s",
                contentUrl,
                node.getMimeType(),
                node.getSize(),
                "TODO-ENCODING",
                "TODO-LOCALE",
                "TODO-ID");

        node.getProperties().putIfAbsent(Content.CONTENT, contentData);
    }

}

