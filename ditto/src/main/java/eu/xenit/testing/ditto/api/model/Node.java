package eu.xenit.testing.ditto.api.model;

import java.util.Set;

public interface Node {

    long getNodeId();
    NodeReference getNodeRef();
    QName getType();

    String getName();

    NodeProperties getProperties();
    Set<QName> getAspects();

}
