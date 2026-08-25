import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileAppender implements LogAppender {
    private final String filePath;
    private LogLevel level;
    private LogFormatter formatter;

    public FileAppender(String filePath, LogLevel level, LogFormatter formatter) {
        this.filePath = filePath;
        this.level = level;
        this.formatter = formatter;
    }

    @Override
    public synchronized void append(LogMessage message) {
        if (message.getLevel().isGreaterOrEqual(this.level)) {
            String formattedMessage = formatter.format(message);
            try (FileWriter fw = new FileWriter(filePath, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println(formattedMessage);
            } catch (IOException e) {
                // Fallback to console on file write failure
                System.err.println("⚠️ [FileAppender Error] Failed to write to " + filePath + ": " + e.getMessage());
                System.err.println("   [Fallback Console] " + formattedMessage);
            }
        }
    }

    @Override public LogLevel getLevel() { return level; }
    @Override public void setLevel(LogLevel level) { this.level = level; }
    @Override public void setFormatter(LogFormatter formatter) { this.formatter = formatter; }
    public String getFilePath() { return filePath; }
}
