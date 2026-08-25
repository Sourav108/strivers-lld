package repository;

import domain.Task;
import domain.TaskSearchCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TaskRepositoryImpl implements TaskRepository {
    private final Map<Integer, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(int id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public void delete(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            // Cascade delete subtasks recursively
            for (Task sub : task.getAllSubtasks()) {
                tasks.remove(sub.getId());
            }
        }
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> findByAssignee(int assigneeId) {
        return tasks.values().stream()
                .filter(t -> t.getAssigneeId() != null && t.getAssigneeId() == assigneeId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByParentTask(int parentTaskId) {
        return tasks.values().stream()
                .filter(t -> t.getParentTaskId() != null && t.getParentTaskId() == parentTaskId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> search(TaskSearchCriteria criteria) {
        return tasks.values().stream()
                .filter(t -> criteria.getAssigneeId() == null || (t.getAssigneeId() != null && t.getAssigneeId().equals(criteria.getAssigneeId())))
                .filter(t -> criteria.getCreatorId() == null || t.getCreatorId() == criteria.getCreatorId())
                .filter(t -> criteria.getPriority() == null || t.getPriority() == criteria.getPriority())
                .filter(t -> criteria.getStatus() == null || t.getStatus() == criteria.getStatus())
                .filter(t -> criteria.getDueDateRange() == null || criteria.getDueDateRange().includes(t.getDueDate()))
                .filter(t -> criteria.getTags().isEmpty() || t.getTags().containsAll(criteria.getTags()))
                .filter(t -> criteria.getHasSubtasks() == null || (criteria.getHasSubtasks() ? t.hasSubtasks() : !t.hasSubtasks()))
                .collect(Collectors.toList());
    }
}
