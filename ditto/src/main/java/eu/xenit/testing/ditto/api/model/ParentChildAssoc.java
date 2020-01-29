package eu.xenit.testing.ditto.api.model;

public interface ParentChildAssoc {

    QName getAssocTypeQName();
    Node getParent();
    Node getChild();

    boolean isPrimary();
    int getNthSibling();

}
