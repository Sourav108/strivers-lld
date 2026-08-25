package domain;

import java.util.ArrayList;
import java.util.List;

public class TaskSearchCriteria {
    private Integer assigneeId;
    private Integer creatorId;
    private Priority priority;
    private TaskStatus status;
    private DataRange dueDateRange;
    private List<String> tags = new ArrayList<>();
    private Boolean hasSubtasks;
    private String sortBy = "priority"; // default
    private String sortOrder = "desc";  // default

    public Integer getAssigneeId() { return assigneeId; }
    public Integer getCreatorId() { return creatorId; }
    public Priority getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public DataRange getDueDateRange() { return dueDateRange; }
    public List<String> getTags() { return tags; }
    public Boolean getHasSubtasks() { return hasSubtasks; }
    public String getSortBy() { return sortBy; }
    public String getSortOrder() { return sortOrder; }

    public TaskSearchCriteria assigneeId(Integer assigneeId) { this.assigneeId = assigneeId; return this; }
    public TaskSearchCriteria creatorId(Integer creatorId) { this.creatorId = creatorId; return this; }
    public TaskSearchCriteria priority(Priority priority) { this.priority = priority; return this; }
    public TaskSearchCriteria status(TaskStatus status) { this.status = status; return this; }
    public TaskSearchCriteria dueDateRange(DataRange dueDateRange) { this.dueDateRange = dueDateRange; return this; }
    public TaskSearchCriteria tags(List<String> tags) { this.tags = tags; return this; }
    public TaskSearchCriteria hasSubtasks(Boolean hasSubtasks) { this.hasSubtasks = hasSubtasks; return this; }
    public TaskSearchCriteria sortBy(String sortBy) { this.sortBy = sortBy; return this; }
    public TaskSearchCriteria sortOrder(String sortOrder) { this.sortOrder = sortOrder; return this; }
}
