package client.utils;

import javafx.scene.text.Text;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatter {

    private static DateTimeFormatter joiningDateFormatter =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    private static DateTimeFormatter postingDateMDY =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private static DateTimeFormatter postingDateMD =
            DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH);

    private static DateTimeFormatter postingDateInPage =
            DateTimeFormatter.ofPattern("hh:mm a . MMM dd, yyyy", Locale.ENGLISH);


    public static String joiningDate(Timestamp ts) {
        return ts.toLocalDateTime().format(joiningDateFormatter);
    }

    public static String postingDate(Timestamp ts) {
        LocalDateTime dt = ts.toLocalDateTime();
        String date;
        if (dt.getYear() == LocalDateTime.now().getYear()) {
            if (dt.getMonthValue() == LocalDateTime.now().getMonthValue() &&
                    dt.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                long hours = Duration.between(dt, LocalDateTime.now()).toHours();
                date = hours + "h";
            }
            else {
                date = dt.format(postingDateMD);
            }
        }
        else {
            date = dt.format(postingDateMDY);
        }
        return date;
    }

    public static String postingDateInPage(Timestamp ts) { return ts.toLocalDateTime().format(postingDateInPage); }
}
