package core;

import java.util.List;

public interface Logger {
    void debug(String message);
    void info(String message);
    void warning(String message);
    void error(String message);
    void fatal(String message);

    void debug(String message, String source);
    void info(String message, String source);
    void warning(String message, String source);
    void error(String message, String source);
    void fatal(String message, String source);

    void log(LogLevel level, String message);
    void log(LogLevel level, String message, String source);

    void setLevel(LogLevel level);
    LogLevel getLevel();
    String getName();

    void addAppender(LogAppender appender);
    void removeAppender(LogAppender appender);
    List<LogAppender> getAppenders();

    void addFilter(LogFilter filter);
    void removeFilter(LogFilter filter);
    List<LogFilter> getFilters();
}
