package eu.xenit.testing.ditto.internal;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.ParentChildAssoc;
import eu.xenit.testing.ditto.api.model.QName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class DefaultParentChildAssoc implements ParentChildAssoc {

    private final Node parent;
    private final QName assocTypeQName;
    private final Node child;
    private final boolean primary;

    public int getNthSibling() {
        return -1;
    }
}
