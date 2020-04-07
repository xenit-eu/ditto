package eu.xenit.testing.ditto.internal;


import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.model.ContentData;
import eu.xenit.testing.ditto.api.model.ParentChildAssoc;
import eu.xenit.testing.ditto.api.model.PeerAssoc;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.DefaultNode.NodeContext;
import eu.xenit.testing.ditto.internal.content.ContentUrlProviderSpi;
import eu.xenit.testing.ditto.internal.content.DefaultContentData;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NodeInitializer {


    private final Set<QName> auditableBlackListTypes = new HashSet<>(Collections.singletonList(System.STORE_ROOT));
    private final String DEFAULT_LOCALE = "en_US";

    public void accept(DefaultNode node, NodeContext context) {

        this.setDefaultSystemProperties(node);
        this.setDefaultContentModelProperties(node);
        this.setAuditableAspectAndProps(node, context);
        this.setContentData(node, context);

        this.setupBiDirectionalParentAssoc(node);
        this.setupBiDrectionalPeerAssocs(node);
    }

    // FIXME the child collections should NOT be mutable through their public API !!
    private void setupBiDirectionalParentAssoc(DefaultNode node) {
        ParentChildAssoc parentAssoc = node.getPrimaryParentAssoc();
        if (parentAssoc == null) {
            return;
        }

        parentAssoc.getParent()
                .getChildNodeCollection()
                .addAssociation(parentAssoc);
    }

    private void setupBiDrectionalPeerAssocs(DefaultNode node) {
        // node as the target of associations
        // put assocs from the sourceAssocsCollection on the targetAssocsCollection of the source node
        List<PeerAssoc> sourceAssociations = node.getSourceAssociationCollection().getAssociations().collect(
                Collectors.toList());
        for (PeerAssoc assoc : sourceAssociations) {
            assoc.getSourceNode().getTargetAssociationCollection().addAssociation(assoc);
        }
        // node as the source of associations
        // put assocs from the targetAssocsCollection on the sourceAssocsCollection of the target node
        List<PeerAssoc> targetAssociations = node.getTargetAssociationCollection().getAssociations().collect(
                Collectors.toList());
        for (PeerAssoc assoc : targetAssociations) {
            assoc.getTargetNode().getSourceAssociationCollection().addAssociation(assoc);
        }
    }

    private void setDefaultSystemProperties(DefaultNode node) {
        // inherited from type sys:base
        node.getAspects().add(System.REFERENCEABLE);
        node.getProperties().put(System.NODE_DBID, node.getNodeId());
        node.getProperties().putIfAbsent(System.STORE_PROTOCOL, node.getNodeRef().getStoreProtocol());
        node.getProperties().putIfAbsent(System.STORE_IDENTIFIER, node.getNodeRef().getStoreIdentifier());
        node.getProperties().putIfAbsent(System.NODE_UUID, node.getNodeRef().getUuid());

        // inherited from type sys:base
        node.getAspects().add(System.LOCALIZED);
        node.getProperties().putIfAbsent(System.LOCALE, DEFAULT_LOCALE);
    }

    private void setDefaultContentModelProperties(DefaultNode node) {
        // If the name is missing, use the uuid
        node.getProperties().putIfAbsent(Content.NAME, node.getNodeRef().getUuid());

        // title
        // description
    }

    /**
     * Initialize auditable aspects & properties for a node. Given that dictionary-model type inheritance is not yet
     * supported here, we fall back to a blacklist of types where we should NOT inject auditable props
     */
    private void setAuditableAspectAndProps(DefaultNode node, NodeContext context) {

        if (auditableBlackListTypes.contains(node.getType())) {
            return;
        }

        node.getAspects().add(Content.AUDITABLE);

        node.getProperties().putIfAbsent(Content.CREATOR, "System");
        node.getProperties().putIfAbsent(Content.CREATED, context.getInstant().toString());
        node.getProperties().putIfAbsent(Content.MODIFIER, "System");
        node.getProperties().putIfAbsent(Content.MODIFIED, context.getInstant().toString());
    }

    private void setContentData(DefaultNode node, NodeContext context) {
        if (context.getContentDataMap().isEmpty()) {
            return;
        }

        context.getContentDataMap().forEach((key, builder) -> {
            ContentUrlProviderSpi contentUrlProvider = context.getContentUrlProvider();
            String contentUrl = contentUrlProvider.createContentUrl(node, context);

            // TODO what about contentId ?!

            Supplier<InputStream> contentDelegate;
            if (builder.data() != null) {
                // The builder provided the content
                contentDelegate = () -> new ByteArrayInputStream(builder.data());
            } else {
                // Hooking up some fake data generator, using the node-id as a random seed
                // This means content should be "random", but stable
                long seed = node.getNodeId();
                contentDelegate = () -> {
                    String fakeContent = "foo with seed: '" + seed + "'";
                    Charset encoding = builder.getEncodingOrDefault();
                    return new ByteArrayInputStream(fakeContent.getBytes(encoding));
                };
            }

            // Create content-data
            ContentData contentData = new DefaultContentData(contentDelegate,
                    contentUrl, builder.mimetype(), builder.size(),
                    builder.getEncodingOrDefault().name(), builder.locale(), context.nextContentDataId());

            // Save it in the node properties
            Serializable old = node.getProperties().putIfAbsent(key, contentData);

            //.sanity check
            if (old != null) {
                String conflictExMsg = String.format("Conflict: property '%s' already exists: %s", key, old);
                throw new UnsupportedOperationException(conflictExMsg);
            }
        });
    }

}

