package domain;

import java.time.LocalDateTime;

public class TaskChangeLog {
    private final int id;
    private final int taskId;
    private final int userId;
    private final ChangeType changeType;
    private final String oldValue;
    private final String newValue;
    private final LocalDateTime timestamp;

    public TaskChangeLog(int id, int taskId, int userId, ChangeType changeType, String oldValue, String newValue) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = LocalDateTime.now();
    }

    public int getId() { return id; }
    public int getTaskId() { return taskId; }
    public int getUserId() { return userId; }
    public ChangeType getChangeType() { return changeType; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "AuditLog[" + timestamp + "] Task #" + taskId + " " + changeType + ": '" + oldValue + "' -> '" + newValue + "' (by user #" + userId + ")";
    }
}
