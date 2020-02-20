package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.model.PeerAssoc;
import eu.xenit.testing.ditto.api.model.PeerAssocCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DefaultPeerAssocCollection implements PeerAssocCollection {

    private final Type type;
    private final List<PeerAssoc> assocs;

    public DefaultPeerAssocCollection(Type type, List<PeerAssoc> peerAssocList) {
        Objects.requireNonNull(type, "Argument 'type' is required");
        Objects.requireNonNull(peerAssocList, "Argument 'peerAssocList' is required");
        this.type = type;
        this.assocs = peerAssocList;
    }

    public DefaultPeerAssocCollection(Type type) {
        this(type, new ArrayList<>());
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Stream<PeerAssoc> getAssociations() {
        return assocs.stream();
    }

    @Override
    public boolean addAssociation(PeerAssoc peerAssoc) {
        if (assocs.contains(peerAssoc)) {
            return false;
        }
        return assocs.add(peerAssoc);
    }
}
