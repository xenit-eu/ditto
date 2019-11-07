package eu.xenit.testing.ditto.alfresco;

import eu.xenit.testing.ditto.alfresco.internal.ContentDataParser;
import eu.xenit.testing.ditto.alfresco.internal.ContentDataParser.ContentDataField;
import eu.xenit.testing.ditto.util.Assert;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class NodeContainer {

    private final HashMap<NodeReference, Node> nodesByNodeRef = new LinkedHashMap<>();
    private final HashMap<Long, Node> nodesById = new LinkedHashMap<>();
    private final HashMap<String, Node> contentLookup = new LinkedHashMap<>();

    public NodeContainer() {
    }

    public void add(Node node) {
        Node conflict = this.nodesById.putIfAbsent(node.getNodeId(), node);
        if (conflict != null) {
            throw new IllegalArgumentException("Node with id " + node.getNodeId() + " already exists");
        }

        this.nodesByNodeRef.put(node.getNodeRef(), node);

        node.getProperties().getContentData().ifPresent(contentData -> {
            String contentUrl = ContentDataParser.extractField(contentData, ContentDataField.CONTENT_URL);
            this.contentLookup.put(contentUrl, node);
        });
    }

    public void delete(Node node) {
        Long nodeId = node.getNodeId();

        Node removed = this.nodesById.remove(nodeId);
        if (!Objects.equals(node.getNodeRef(), removed.getNodeRef())) {
            String msg = String.format("Data store inconsistency detected, noderef mismatch: %s vs %s",
                    node.getNodeRef(), removed.getNodeRef());
            throw new IllegalArgumentException(msg);
        }

        this.nodesByNodeRef.remove(node.getNodeRef());

        node.getProperties().getContentData().ifPresent(contentData -> {
            String contentUrl = ContentDataParser.extractField(contentData, ContentDataField.CONTENT_URL);
            this.contentLookup.remove(contentUrl);
        });
    }

    public Optional<Node> getNode(String nodeRef)
    {
        Assert.hasText(nodeRef, "Argument 'nodeRef' should not be empty or null");
        return this.getNode(NodeReference.parse(nodeRef));
    }

    public Optional<Node> getNode(NodeReference nodeRef) {
        Objects.requireNonNull(nodeRef, "Argument 'nodeRef' is required");
        return Optional.of(this.nodesByNodeRef.get(nodeRef));
    }

    public Optional<Node> getNodeByContentUrl(String contentUrl) {
        Objects.requireNonNull(contentUrl, "Argument 'contentUrl' is required");
        return Optional.ofNullable(this.contentLookup.get(contentUrl));
    }

    public Stream<Node> stream() {
        return this.nodesById.values().stream();
    }
}
