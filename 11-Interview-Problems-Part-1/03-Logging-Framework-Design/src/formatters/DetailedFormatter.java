package formatters;

import core.LogFormatter;
import core.LogMessage;
import java.time.format.DateTimeFormatter;

public class DetailedFormatter implements LogFormatter {
    private static final DateTimeFormatter DETAILED_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String format(LogMessage message) {
        return String.format("[%s] [%s] [Source: %s] [Thread: %s] - %s",
                message.getLevel(),
                message.getTimestamp().format(DETAILED_DATE_FORMAT),
                message.getSource(),
                Thread.currentThread().getName(),
                message.getMessage());
    }
}
