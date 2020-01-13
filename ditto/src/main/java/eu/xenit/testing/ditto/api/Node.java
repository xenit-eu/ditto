package eu.xenit.testing.ditto.api;

import java.util.Set;

public interface Node {

    long getNodeId();
    NodeReference getNodeRef();
    String getType();

    String getName();

    NodeProperties getProperties();
    Set<String> getAspects();

}
