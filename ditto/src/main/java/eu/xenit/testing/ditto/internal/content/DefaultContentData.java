package eu.xenit.testing.ditto.internal.content;

import eu.xenit.testing.ditto.api.ContentData;
import java.io.InputStream;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultContentData implements InternalContentData, ContentData {

    private Supplier<InputStream> contentDelegate;

    private String contentUrl;
    private String mimeType;
    private long size;
    private String encoding;
    private String locale;
}
