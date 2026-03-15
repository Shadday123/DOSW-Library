
package edu.eci.dosw.DOSW_Library.core.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Date now() {
        return new Date();
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    public static long getDaysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
    }

    public static boolean isOverdue(Date returnDate) {
        if (returnDate == null) {
            return false;
        }
        return returnDate.before(now());
    }

    public static long getOverdueDays(Date returnDate) {
        if (!isOverdue(returnDate)) {
            return 0;
        }
        return getDaysBetween(returnDate, now());
    }

    public static Date getDatePlusDays(int days) {
        return addDays(now(), days);
    }
}
