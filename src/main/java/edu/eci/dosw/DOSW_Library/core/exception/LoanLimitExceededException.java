package edu.eci.dosw.DOSW_Library.core.exception;

public class LoanLimitExceededException extends RuntimeException {

    public LoanLimitExceededException(String message) {
        super(message);
    }

    public LoanLimitExceededException(String userId, int limit) {
        super("El usuario con ID '" + userId + "' ha alcanzado el límite de " + limit + " préstamos activos");
    }

    public LoanLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}