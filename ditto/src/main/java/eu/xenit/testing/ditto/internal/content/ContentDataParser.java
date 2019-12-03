package eu.xenit.testing.ditto.internal.content;

import eu.xenit.testing.ditto.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ContentDataParser {

    public enum ContentDataField {
        CONTENT_URL("contentUrl"),
        MIME_TYPE("mimetype"),
        SIZE("size");

        ContentDataField(String key) {
            this.key = key;
        }

        private String key;

        public String getKey() {
            return key;
        }
    }

    public static String extractField(final String contentData, final ContentDataField field) {
        if (StringUtils.nullOrEmpty(contentData)) {
            return null;
        }

        Matcher matcher = Pattern.compile(field.getKey() + "=([^|]*)").matcher(contentData);

        if (!matcher.find()) {
            return null;
        }

        return matcher.group().replace(field.getKey() + "=", "");
    }
}