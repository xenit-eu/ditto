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

    @Override
    public String toString(){
        return new StringBuilder()
                .append("contentUrl=").append(this.getContentUrl())
                .append("|mimetype=").append(this.getMimeType())
                .append("|size=").append(this.getSize())
                .append("|encoding=").append(this.getEncoding())
                .append("|locale=").append(this.getLocale())
//                .append("|id=").append()
                .toString();
    }
}
