package io.vold.ankh.utils;

public final class ServerUtils {

    public static final Integer MIN_SUPPORTED_JAVA_VERSION = 21;

    private ServerUtils() {}

    public static void checkJavaVersion() {
        var version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) { version = version.substring(0, dot); }
        }
        int versionNumber = Integer.parseInt(version);
        if (versionNumber < MIN_SUPPORTED_JAVA_VERSION) {
            throw new UnsupportedOperationException("Ankh Server requires Java 21 or later. Current version: " + version);
        }
    }
}
