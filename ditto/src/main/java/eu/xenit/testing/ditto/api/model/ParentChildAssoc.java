package eu.xenit.testing.ditto.api.model;

public interface ParentChildAssoc extends Association{

    Node getParent();
    Node getChild();

    boolean isPrimary();
    int getNthSibling();

}
