package eu.xenit.testing.ditto.api;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;

public interface NodeConfigurator<T> {

    T setDefaultParentNode(Node node);
    T setDefaultChildAssocType(QName qname);

}
