package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class LoggerImpl implements Logger {
    private static final Map<String, Logger> instances = new ConcurrentHashMap<>();

    private final String name;
    private final LogConfiguration config;
    private final List<LogAppender> appenders = new CopyOnWriteArrayList<>();
    private final List<LogFilter> filters = new CopyOnWriteArrayList<>();

    public LoggerImpl(String name) {
        this(name, new LogConfiguration(LogLevel.DEBUG));
    }

    public LoggerImpl(String name, LogConfiguration config) {
        this.name = name;
        this.config = config;
    }

    public static Logger getLogger(String name) {
        return instances.computeIfAbsent(name, LoggerImpl::new);
    }

    @Override
    public String getName() { return name; }

    @Override
    public LogLevel getLevel() { return config.getRootLevel(); }

    @Override
    public void setLevel(LogLevel level) { config.setRootLevel(level); }

    @Override
    public void addAppender(LogAppender appender) { appenders.add(appender); }

    @Override
    public void removeAppender(LogAppender appender) { appenders.remove(appender); }

    @Override
    public List<LogAppender> getAppenders() { return new ArrayList<>(appenders); }

    @Override
    public void addFilter(LogFilter filter) { filters.add(filter); }

    @Override
    public void removeFilter(LogFilter filter) { filters.remove(filter); }

    @Override
    public List<LogFilter> getFilters() { return new ArrayList<>(filters); }

    @Override
    public void log(LogLevel level, String message) {
        log(level, message, this.name);
    }

    @Override
    public void log(LogLevel level, String message, String source) {
        if (!level.isGreaterOrEqual(config.getRootLevel())) {
            return; // Dropped by root level threshold
        }

        LogMessage logMessage = new LogMessage.Builder()
                .level(level)
                .message(message)
                .source(source != null ? source : this.name)
                .build();

        // Process Filter Chain (Chain of Responsibility)
        for (LogFilter filter : filters) {
            if (!filter.shouldLog(logMessage)) {
                return; // Dropped by filter
            }
        }

        // Dispatch to Appenders (Strategy Pattern)
        for (LogAppender appender : appenders) {
            if (appender.isEnabled(level)) {
                appender.append(logMessage);
            }
        }
    }

    @Override public void debug(String message) { log(LogLevel.DEBUG, message); }
    @Override public void info(String message) { log(LogLevel.INFO, message); }
    @Override public void warning(String message) { log(LogLevel.WARNING, message); }
    @Override public void error(String message) { log(LogLevel.ERROR, message); }
    @Override public void fatal(String message) { log(LogLevel.FATAL, message); }

    @Override public void debug(String message, String source) { log(LogLevel.DEBUG, message, source); }
    @Override public void info(String message, String source) { log(LogLevel.INFO, message, source); }
    @Override public void warning(String message, String source) { log(LogLevel.WARNING, message, source); }
    @Override public void error(String message, String source) { log(LogLevel.ERROR, message, source); }
    @Override public void fatal(String message, String source) { log(LogLevel.FATAL, message, source); }
}
