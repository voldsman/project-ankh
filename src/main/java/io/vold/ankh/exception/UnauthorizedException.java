package io.vold.ankh.exception;

public class UnauthorizedException extends HttpException {
    public UnauthorizedException(String message) {
        super(401, message);
    }
}
