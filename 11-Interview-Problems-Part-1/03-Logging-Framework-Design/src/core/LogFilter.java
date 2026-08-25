package core;

public interface LogFilter {
    boolean shouldLog(LogMessage message);
}
