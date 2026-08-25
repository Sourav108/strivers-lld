package domain;

import java.time.LocalDateTime;

public class Comment {
    private final int id;
    private final int taskId;
    private final int userId;
    private final String content;
    private final LocalDateTime createdAt;

    public Comment(int id, int taskId, int userId, String content) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public int getTaskId() { return taskId; }
    public int getUserId() { return userId; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Comment{" + "id=" + id + ", taskId=" + taskId + ", user=" + userId + ", text='" + content + '\'' + '}';
    }
}
