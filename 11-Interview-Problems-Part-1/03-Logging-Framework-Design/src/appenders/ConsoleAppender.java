package appenders;

import core.LogAppender;
import core.LogFormatter;
import core.LogLevel;
import core.LogMessage;
import formatters.SimpleFormatter;

public class ConsoleAppender implements LogAppender {
    private LogLevel level;
    private LogFormatter formatter;

    public ConsoleAppender() {
        this(LogLevel.DEBUG, new SimpleFormatter());
    }

    public ConsoleAppender(LogLevel level, LogFormatter formatter) {
        this.level = level;
        this.formatter = formatter;
    }

    @Override
    public synchronized void append(LogMessage message) {
        if (isEnabled(message.getLevel())) {
            String formattedMessage = formatter.format(message);
            if (message.getLevel().isGreaterOrEqual(LogLevel.ERROR)) {
                System.err.println(formattedMessage);
            } else {
                System.out.println(formattedMessage);
            }
        }
    }

    @Override public LogLevel getLevel() { return level; }
    @Override public void setLevel(LogLevel level) { this.level = level; }
    @Override public void setFormatter(LogFormatter formatter) { this.formatter = formatter; }
    @Override public LogFormatter getFormatter() { return formatter; }
    @Override public boolean isEnabled(LogLevel level) { return level.isGreaterOrEqual(this.level); }
}
