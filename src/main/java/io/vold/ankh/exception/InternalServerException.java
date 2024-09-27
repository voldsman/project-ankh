package io.vold.ankh.exception;

public class InternalServerException extends HttpException {
    public InternalServerException(String message) {
        super(500, message);
    }
}
