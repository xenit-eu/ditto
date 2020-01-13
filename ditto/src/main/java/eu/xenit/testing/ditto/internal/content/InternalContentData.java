package eu.xenit.testing.ditto.internal.content;

import eu.xenit.testing.ditto.api.ContentData;
import java.io.InputStream;
import java.util.function.Supplier;

public interface InternalContentData extends ContentData {

    Supplier<InputStream> getContentDelegate();

}
