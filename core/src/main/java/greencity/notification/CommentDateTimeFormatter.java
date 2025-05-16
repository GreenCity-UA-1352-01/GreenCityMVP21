package greencity.notification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CommentDateTimeFormatter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    public static String format(ZonedDateTime dateTime) {
        ZonedDateTime now = ZonedDateTime.now(dateTime.getZone());

        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        LocalDate today = now.toLocalDate();
        LocalDate yesterday = today.minusDays(1);

        String dateString;
        if (date.equals(today)) {
            dateString = "Today";
        } else if (date.equals(yesterday)) {
            dateString = "Yesterday";
        } else {
            return DATE_FORMATTER.format(date);
        }

        return String.format("%s %s", dateString, TIME_FORMATTER.format(time).toLowerCase());
    }
}
