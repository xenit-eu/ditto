package eu.xenit.testing.ditto.internal.content;

import eu.xenit.testing.ditto.api.content.SwarmContentServiceCustomizer;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class SwarmContentServiceConfiguration implements SwarmContentServiceCustomizer {

    private String bucket;

}
