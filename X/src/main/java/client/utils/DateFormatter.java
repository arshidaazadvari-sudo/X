package client.utils;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatter {

    private static DateTimeFormatter joiningDateFormatter =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    public static String joiningDateString(Timestamp ts) {
        return ts.toLocalDateTime().format(joiningDateFormatter);
    }
}
