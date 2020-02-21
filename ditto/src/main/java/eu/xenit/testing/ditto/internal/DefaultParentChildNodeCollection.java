package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.ParentChildAssoc;
import eu.xenit.testing.ditto.api.model.ParentChildNodeCollection;
import eu.xenit.testing.ditto.api.model.QName;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class DefaultParentChildNodeCollection implements ParentChildNodeCollection {

    @Getter
    private final Node parent;

    private final HashMap<ChildId, ParentChildAssoc> associations;

    DefaultParentChildNodeCollection(Node parent) {
        this(parent, Collections.emptyList());
    }

    DefaultParentChildNodeCollection(Node parent, Collection<ParentChildAssoc> assocs) {

        Objects.requireNonNull(parent, "Argument 'parent' is required");
        Objects.requireNonNull(assocs, "Argument 'assocs' is required");
        this.parent = parent;

        this.associations = new LinkedHashMap<>(assocs.stream().collect(Collectors.toMap(
                ChildId::from,
                assoc -> assoc
        )));
    }

    @Override
    public Stream<ParentChildAssoc> getAssociations() {
        return this.associations.values().stream();
    }

    @Override
    public Stream<Node> getChilds(QName assocType) {
        return this.getAssociations()
                .filter(assoc -> assoc.getAssocTypeQName().equals(assocType))
                .map(ParentChildAssoc::getChild);
    }

    @Override
    public Optional<Node> getChild(QName assocType, QName childQName) {
        Objects.requireNonNull(assocType, "Argument 'assocType' is required");
        Objects.requireNonNull(childQName, "Argument 'childQName' is required");

        return Optional.ofNullable(this.associations.get(new ChildId(assocType, childQName)))
                .map(ParentChildAssoc::getChild);
    }


    @Override
    public boolean addAssociation(ParentChildAssoc assoc) {
        Objects.requireNonNull(assoc, "Argument 'assoc' is required");
        ParentChildAssoc existing = this.associations.putIfAbsent(ChildId.from(assoc), assoc);
        return existing == null;
    }

    @Override
    public boolean addChild(Node child, QName assocType) {
        Objects.requireNonNull(child, "Argument 'child' is required");
        Objects.requireNonNull(assocType, "Argument 'assocType' is required");

        return this.addAssociation(new DefaultParentChildAssoc(this.getParent(), assocType, child, true));
    }


    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class ChildId {
        private final QName assocType;
        private final QName childQName;

        public static ChildId from(ParentChildAssoc assoc) {
            return new ChildId(assoc.getAssocTypeQName(), assoc.getChild().getQName());
        }
    }
}