package core;

import java.time.LocalDateTime;

public class LogMessage {
    private final LocalDateTime timestamp;
    private final LogLevel level;
    private final String message;
    private final String source;

    private LogMessage(Builder builder) {
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.level = builder.level;
        this.message = builder.message;
        this.source = builder.source;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public LogLevel getLevel() { return level; }
    public String getMessage() { return message; }
    public String getSource() { return source; }

    public static class Builder {
        private LocalDateTime timestamp;
        private LogLevel level = LogLevel.INFO;
        private String message;
        private String source = "Application";

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public LogMessage build() {
            if (message == null || message.trim().isEmpty()) {
                throw new IllegalArgumentException("Log message text must not be null or blank.");
            }
            return new LogMessage(this);
        }
    }
}
