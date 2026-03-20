package edu.eci.dosw.DOSW_Library.core.exception;

public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(String message) {
        super(message);
    }

    public BookNotAvailableException(String bookId, String title) {
        super("El libro '" + title + "' (ID: " + bookId + ") no tiene copias disponibles");
    }

    public BookNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}