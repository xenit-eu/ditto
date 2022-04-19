package eu.xenit.testing.ditto.util;

public class Assert {

    private Assert() {
        // do not instantiate
    }

    public static void hasText(String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }
}
