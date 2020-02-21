package eu.xenit.testing.ditto.api.model;

public interface PeerAssoc extends Association {

    Node getSourceNode();
    Node getTargetNode();

}
