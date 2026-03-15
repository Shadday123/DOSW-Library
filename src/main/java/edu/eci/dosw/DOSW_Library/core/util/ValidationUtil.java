
package edu.eci.dosw.DOSW_Library.core.util;

public class ValidationUtil {

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (!isNotEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " no puede estar vacío");
        }
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }

    public static void validateNotNull(Object object, String objectName) {
        if (object == null) {
            throw new IllegalArgumentException(objectName + " no puede ser nulo");
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static void validateCopies(int availableCopies, int totalCopies) {
        if (availableCopies < 0 || totalCopies < 0) {
            throw new IllegalArgumentException("Las copias no pueden ser negativas");
        }
        if (availableCopies > totalCopies) {
            throw new IllegalArgumentException("Las copias disponibles no pueden exceder el total");
        }
    }
}

