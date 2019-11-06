package eu.xenit.testing.ditto.alfresco;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
@AllArgsConstructor
public class NodeReference {

    public static final String URI_FILLER = "://";
    public static final String STOREREF_PROT_WORKSPACE = "workspace";
    public static final String STOREREF_ID_SPACESSTORE = "SpacesStore";

    @NonNull
    @Getter
    private final String storeProtocol;

    @NonNull
    @Getter
    private final String storeIdentifier;

    @NonNull
    @Getter
    private final String uuid;

    public static NodeReference parse(String nodeRef) {
        int lastForwardSlash = nodeRef.lastIndexOf('/');
        if (lastForwardSlash == -1) {
            throw new IllegalArgumentException("Invalid node ref - does not contain forward slash: " + nodeRef);
        }

        String storeRef = nodeRef.substring(0, lastForwardSlash);
        int dividerPatternPosition = storeRef.indexOf("://");
        if (dividerPatternPosition == -1) {
            throw new IllegalArgumentException("Invalid store ref: Does not contain " + URI_FILLER + "   " + storeRef);
        }

        return new NodeReference(
                storeRef.substring(0, dividerPatternPosition),
                storeRef.substring(dividerPatternPosition + 3),
                nodeRef.substring(lastForwardSlash + 1)
        );
    }

    @Override
    public String toString() {
        return String.format("%s://%s/%s", storeProtocol, storeIdentifier, uuid);
    }

}
