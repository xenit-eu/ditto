package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.PeerAssoc;
import eu.xenit.testing.ditto.api.model.QName;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultPeerAssoc implements PeerAssoc {

    @Getter
    private final Node sourceNode;

    @Getter
    private final Node targetNode;

    @Getter
    private final QName assocTypeQName;


    public DefaultPeerAssoc(Node sourceNode, Node targetNode, QName assocTypeQName) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.assocTypeQName = assocTypeQName;

    }

}
