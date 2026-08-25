package core;

public class LogConfiguration {
    private LogLevel rootLevel;

    public LogConfiguration() {
        this.rootLevel = LogLevel.DEBUG;
    }

    public LogConfiguration(LogLevel rootLevel) {
        this.rootLevel = rootLevel;
    }

    public LogLevel getRootLevel() {
        return rootLevel;
    }

    public void setRootLevel(LogLevel rootLevel) {
        this.rootLevel = rootLevel;
    }

    @Override
    public String toString() {
        return "LogConfiguration{rootLevel=" + rootLevel + '}';
    }
}
