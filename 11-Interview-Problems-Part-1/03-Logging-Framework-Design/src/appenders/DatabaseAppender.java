package appenders;

import core.LogAppender;
import core.LogFormatter;
import core.LogLevel;
import core.LogMessage;
import formatters.SimpleFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseAppender implements LogAppender {
    private final String tableName;
    private LogLevel level;
    private LogFormatter formatter;
    private final List<String> databaseRecords = Collections.synchronizedList(new ArrayList<>());
    private boolean simulateConnectionFailure = false;

    public DatabaseAppender(String tableName) {
        this(tableName, LogLevel.ERROR, new SimpleFormatter());
    }

    public DatabaseAppender(String tableName, LogLevel level, LogFormatter formatter) {
        this.tableName = tableName;
        this.level = level;
        this.formatter = formatter;
    }

    public void setSimulateConnectionFailure(boolean failure) {
        this.simulateConnectionFailure = failure;
    }

    @Override
    public synchronized void append(LogMessage message) {
        if (isEnabled(message.getLevel())) {
            String formattedMessage = formatter.format(message);
            if (simulateConnectionFailure) {
                System.err.println("⚠️ [DatabaseAppender Error] Connection to table '" + tableName + "' timed out.");
                System.err.println("   [Fallback Console] " + formattedMessage);
                return;
            }
            databaseRecords.add(formattedMessage);
            System.out.println("🗄️ [DatabaseAppender] Stored in table '" + tableName + "': " + formattedMessage);
        }
    }

    public List<String> getDatabaseRecords() {
        return new ArrayList<>(databaseRecords);
    }

    @Override public LogLevel getLevel() { return level; }
    @Override public void setLevel(LogLevel level) { this.level = level; }
    @Override public void setFormatter(LogFormatter formatter) { this.formatter = formatter; }
    @Override public LogFormatter getFormatter() { return formatter; }
    @Override public boolean isEnabled(LogLevel level) { return level.isGreaterOrEqual(this.level); }
    public String getTableName() { return tableName; }
}
