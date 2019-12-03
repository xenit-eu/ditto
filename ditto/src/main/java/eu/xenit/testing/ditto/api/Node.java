package eu.xenit.testing.ditto.api;

public interface Node {

    String getMimeType();

    String getName();

    long getNodeId();

    NodeReference getNodeRef();

    String getType();

    NodeProperties getProperties();

    java.util.Set<String> getAspects();

    long getSize();

    byte[] getContent();
}
