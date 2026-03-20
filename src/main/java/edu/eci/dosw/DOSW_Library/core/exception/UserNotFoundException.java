package edu.eci.dosw.DOSW_Library.core.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String userId, boolean byId) {
        super("Usuario no encontrado con ID: " + userId);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}