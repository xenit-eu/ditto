package eu.xenit.testing.ditto.internal.content;

import eu.xenit.testing.ditto.api.model.ContentData;
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
    private long id;

    @Override
    public String toString() {
        return "contentUrl=" + this.getContentUrl()
                + "|mimetype=" + this.getMimeType()
                + "|size=" + this.getSize()
                + "|encoding=" + this.getEncoding()
                + "|locale=" + this.getLocale()
                + "|id=" + this.getId();
    }
}
