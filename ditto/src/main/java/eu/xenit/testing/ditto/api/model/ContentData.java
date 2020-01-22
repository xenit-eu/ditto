package eu.xenit.testing.ditto.api.model;

import java.io.Serializable;

public interface ContentData extends Serializable {

    String getContentUrl();

    String getMimeType();
    long getSize();

    String getEncoding();
    String getLocale();
}
