import java.time.format.DateTimeFormatter;

public class SimpleFormatter implements LogFormatter {
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String format(LogMessage message) {
        return String.format("[%s] [%s] - %s",
                message.getLevel(),
                message.getTimestamp().format(DEFAULT_DATE_FORMAT),
                message.getMessage());
    }
}
