
package edu.eci.dosw.DOSW_Library.core.util;

import java.util.UUID;

public class IdGeneratorUtil {

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static String generateIdWithPrefix(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateUserId() {
        return generateIdWithPrefix("USR");
    }

    public static String generateBookId() {
        return generateIdWithPrefix("BK");
    }

    public static String generateLoanId() {
        return generateIdWithPrefix("LN");
    }
}
