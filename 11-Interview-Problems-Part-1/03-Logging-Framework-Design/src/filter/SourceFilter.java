package filter;

import core.LogFilter;
import core.LogMessage;

public class SourceFilter implements LogFilter {
    private final String allowedSourcePrefix;

    public SourceFilter(String allowedSourcePrefix) {
        this.allowedSourcePrefix = allowedSourcePrefix;
    }

    @Override
    public boolean shouldLog(LogMessage message) {
        if (message.getSource() == null) return true;
        return message.getSource().startsWith(allowedSourcePrefix);
    }
}
