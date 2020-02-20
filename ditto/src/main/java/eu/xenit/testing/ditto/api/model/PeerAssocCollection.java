package eu.xenit.testing.ditto.api.model;

import java.util.stream.Stream;

public interface PeerAssocCollection {

    Type getType();

    Stream<PeerAssoc> getAssociations();

    boolean addAssociation(PeerAssoc peerAssoc);

    enum Type {
        SOURCE, TARGET
    }

}
