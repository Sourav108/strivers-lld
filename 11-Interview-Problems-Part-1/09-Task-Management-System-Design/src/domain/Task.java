package domain;

import domain.Observer.TaskSubscriber;
import domain.state.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Task {
    private final int id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Priority priority;
    private TaskStatus status;
    private TaskState state;
    private Integer assigneeId;
    private final int creatorId;
    private Integer parentTaskId;
    private final List<String> tags = new ArrayList<>();
    private final List<Task> subtasks = new CopyOnWriteArrayList<>();
    private final List<TaskSubscriber> subscribers = new CopyOnWriteArrayList<>();
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(int id, String title, String description, LocalDateTime dueDate, Priority priority, int creatorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.creatorId = creatorId;
        this.status = TaskStatus.TODO;
        this.state = new TodoState();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // --- Composite Pattern: Subtask Management ---
    public void addSubtask(Task subtask) {
        subtask.setParentTaskId(this.id);
        this.subtasks.add(subtask);
        // Priority Adjustment: If child has higher priority, bump parent
        if (subtask.getPriority().getLevel() > this.priority.getLevel()) {
            setPriority(subtask.getPriority());
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void removeSubtask(Task subtask) {
        this.subtasks.remove(subtask);
        this.updatedAt = LocalDateTime.now();
    }

    public List<Task> getSubtasks() {
        return Collections.unmodifiableList(subtasks);
    }

    public List<Task> getAllSubtasks() {
        List<Task> all = new ArrayList<>();
        for (Task sub : subtasks) {
            all.add(sub);
            all.addAll(sub.getAllSubtasks());
        }
        return all;
    }

    public boolean hasSubtasks() { return !subtasks.isEmpty(); }
    public int getSubtaskCount() { return subtasks.size(); }

    // --- Observer Pattern: Subscriber Management ---
    public void attach(TaskSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void detach(TaskSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void notifySubscribers(ChangeType changeType, String oldValue, String newValue) {
        for (TaskSubscriber sub : subscribers) {
            sub.update(this.id, changeType, oldValue, newValue);
        }
    }

    // --- State Pattern: Status Transitions ---
    public synchronized void updateStatus(TaskStatus newStatus) {
        if (!state.canTransitionTo(newStatus)) {
            throw new InvalidStateTransitionException("❌ Invalid State Transition: Cannot move Task #" + id +
                    " from " + state.getStateName() + " to " + newStatus);
        }

        String oldStatus = this.status.name();
        this.status = newStatus;
        switch (newStatus) {
            case TODO: this.state = new TodoState(); break;
            case IN_PROGRESS: this.state = new InProgressState(); break;
            case REVIEW: this.state = new ReviewState(); break;
            case COMPLETED: this.state = new CompletedState(); break;
            case CANCELLED: this.state = new CancelledState(); break;
        }
        this.updatedAt = LocalDateTime.now();
        notifySubscribers(ChangeType.STATUS_CHANGED, oldStatus, newStatus.name());
    }

    // --- Getters and Setters ---
    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; this.updatedAt = LocalDateTime.now(); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; this.updatedAt = LocalDateTime.now(); }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; this.updatedAt = LocalDateTime.now(); }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) {
        String oldPriority = this.priority != null ? this.priority.name() : "NONE";
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
        notifySubscribers(ChangeType.PRIORITY_CHANGED, oldPriority, priority.name());
    }

    public TaskStatus getStatus() { return status; }
    public TaskState getState() { return state; }

    public Integer getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Integer assigneeId) {
        String oldAssignee = this.assigneeId != null ? String.valueOf(this.assigneeId) : "UNASSIGNED";
        this.assigneeId = assigneeId;
        this.updatedAt = LocalDateTime.now();
        notifySubscribers(ChangeType.ASSIGNED, oldAssignee, String.valueOf(assigneeId));
    }

    public int getCreatorId() { return creatorId; }
    public Integer getParentTaskId() { return parentTaskId; }
    public void setParentTaskId(Integer parentTaskId) { this.parentTaskId = parentTaskId; }

    public List<String> getTags() { return tags; }
    public void addTag(String tag) { this.tags.add(tag); }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return "Task #" + id + " ['" + title + "' | " + priority + " | " + status + " | Assignee: " + assigneeId + " | Subtasks: " + subtasks.size() + "]";
    }
}
