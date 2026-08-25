public class LevelFilter implements LogFilter {
    private final LogLevel minLevel;

    public LevelFilter(LogLevel minLevel) {
        this.minLevel = minLevel;
    }

    @Override
    public boolean shouldLog(LogMessage message) {
        return message.getLevel().isGreaterOrEqual(minLevel);
    }
}
