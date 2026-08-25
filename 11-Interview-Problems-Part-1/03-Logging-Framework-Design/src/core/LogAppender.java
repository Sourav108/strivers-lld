package core;

public interface LogAppender {
    void append(LogMessage message);
    LogLevel getLevel();
    void setLevel(LogLevel level);
    void setFormatter(LogFormatter formatter);
    LogFormatter getFormatter();
    boolean isEnabled(LogLevel level);
}
