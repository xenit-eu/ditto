package eu.xenit.testing.ditto.api.model;

import java.util.Optional;
import java.util.stream.Stream;

public interface ParentChildNodeCollection {

    Node getParent();

    Stream<ParentChildAssoc> getAssociations();
    Stream<Node> getChilds(QName assocType);

    Optional<Node> getChild(QName assocType, QName childQName);

    /**
     * Adds the specified parent-child-association to the node-collection, if it is not already present.
     *
     * If this ParentChildNodeCollection already contains the element, the call leaves the collection unchanged
     * and returns false.
     *
     * @param association to add to the collection
     * @return true when the assoc has been added or false when it already existed
     */
    boolean addAssociation(ParentChildAssoc association);

    boolean addChild(Node child, QName assocType);
}
