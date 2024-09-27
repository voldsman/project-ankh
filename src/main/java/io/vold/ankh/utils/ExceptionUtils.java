package io.vold.ankh.utils;

public final class ExceptionUtils {

    private ExceptionUtils() {}

    public static String getErrorName(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            default -> "HTTP Error";
        };
    }
}
