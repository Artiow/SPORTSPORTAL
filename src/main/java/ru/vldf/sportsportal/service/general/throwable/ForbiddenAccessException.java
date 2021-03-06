package ru.vldf.sportsportal.service.general.throwable;

public class ForbiddenAccessException extends AbstractAuthorizationException {

    public ForbiddenAccessException(String message) {
        super(message);
    }

    public ForbiddenAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
