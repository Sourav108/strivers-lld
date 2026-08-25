import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Logger {
    private static final Map<String, Logger> instances = new ConcurrentHashMap<>();
    private static final Logger ROOT_LOGGER = new Logger("ROOT");

    private final String name;
    private volatile LogLevel rootLevel = LogLevel.DEBUG;
    private final List<LogAppender> appenders = new CopyOnWriteArrayList<>();
    private final List<LogFilter> filters = new CopyOnWriteArrayList<>();

    public Logger(String name) {
        this.name = name;
    }

    public static Logger getLogger(String name) {
        return instances.computeIfAbsent(name, Logger::new);
    }

    public static Logger getRootLogger() {
        return ROOT_LOGGER;
    }

    public void addAppender(LogAppender appender) {
        appenders.add(appender);
    }

    public void removeAppender(LogAppender appender) {
        appenders.remove(appender);
    }

    public void addFilter(LogFilter filter) {
        filters.add(filter);
    }

    public void removeFilter(LogFilter filter) {
        filters.remove(filter);
    }

    public void setLevel(LogLevel level) {
        this.rootLevel = level;
    }

    public LogLevel getLevel() {
        return rootLevel;
    }

    public String getName() {
        return name;
    }

    // Core Log Method
    public void log(LogLevel level, String message, String source) {
        if (!level.isGreaterOrEqual(this.rootLevel)) {
            return; // Dropped by root log level threshold
        }

        LogMessage logMessage = new LogMessage.Builder()
                .level(level)
                .message(message)
                .source(source != null ? source : this.name)
                .build();

        // 1. Process Filter Chain (Chain of Responsibility)
        for (LogFilter filter : filters) {
            if (!filter.shouldLog(logMessage)) {
                return; // Dropped by filter
            }
        }

        // 2. Dispatch to Appenders (Strategy Pattern)
        for (LogAppender appender : appenders) {
            appender.append(logMessage);
        }
    }

    // Convenience Helper Methods
    public void debug(String message) { log(LogLevel.DEBUG, message, this.name); }
    public void info(String message) { log(LogLevel.INFO, message, this.name); }
    public void warning(String message) { log(LogLevel.WARNING, message, this.name); }
    public void error(String message) { log(LogLevel.ERROR, message, this.name); }
    public void fatal(String message) { log(LogLevel.FATAL, message, this.name); }

    public void debug(String message, String source) { log(LogLevel.DEBUG, message, source); }
    public void info(String message, String source) { log(LogLevel.INFO, message, source); }
    public void warning(String message, String source) { log(LogLevel.WARNING, message, source); }
    public void error(String message, String source) { log(LogLevel.ERROR, message, source); }
    public void fatal(String message, String source) { log(LogLevel.FATAL, message, source); }
}
