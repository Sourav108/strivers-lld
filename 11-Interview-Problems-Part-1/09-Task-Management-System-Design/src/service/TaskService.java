package service;

import domain.ChangeType;
import domain.Priority;
import domain.Task;
import domain.TaskSearchCriteria;
import domain.strategy.CreatedDateSortingStrategy;
import domain.strategy.DueDateSortingStrategy;
import domain.strategy.PrioritySortingStrategy;
import domain.strategy.TaskSortingStrategy;
import repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskNotificationService notificationService;

    public TaskService(TaskRepository taskRepository, TaskNotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    public Task createTask(int id, String title, String description, LocalDateTime dueDate, Priority priority, int creatorId) {
        Task task = new Task(id, title, description, dueDate, priority, creatorId);
        taskRepository.save(task);
        notificationService.logChange(id, creatorId, ChangeType.CREATED, "NONE", title);
        System.out.println("📝 [Task Created] " + task);
        return task;
    }

    public Task addSubtask(int parentTaskId, int subtaskId, String title, String description, LocalDateTime dueDate, Priority priority, int creatorId) {
        Task parentTask = taskRepository.findById(parentTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Parent Task #" + parentTaskId + " not found."));

        Task subtask = new Task(subtaskId, title, description, dueDate, priority, creatorId);
        parentTask.addSubtask(subtask);
        taskRepository.save(subtask);
        taskRepository.save(parentTask);

        notificationService.logChange(parentTaskId, creatorId, ChangeType.UPDATED, "SubtaskCount", String.valueOf(parentTask.getSubtaskCount()));
        System.out.println("   ↳ 🌿 [Subtask Added] Parent #" + parentTaskId + " -> Subtask: " + subtask);
        return subtask;
    }

    public void deleteTask(int taskId) {
        taskRepository.delete(taskId);
        System.out.println("🗑️ [Task Deleted] Cascade deleted Task #" + taskId + " and all nested subtasks.");
    }

    public Optional<Task> getTask(int taskId) {
        return taskRepository.findById(taskId);
    }

    public List<Task> searchTasks(TaskSearchCriteria criteria) {
        List<Task> matchingTasks = taskRepository.search(criteria);

        // Select Sorting Strategy (Strategy Pattern)
        TaskSortingStrategy strategy;
        if ("dueDate".equalsIgnoreCase(criteria.getSortBy())) {
            strategy = new DueDateSortingStrategy();
        } else if ("createdDate".equalsIgnoreCase(criteria.getSortBy())) {
            strategy = new CreatedDateSortingStrategy();
        } else {
            strategy = new PrioritySortingStrategy();
        }

        boolean ascending = "asc".equalsIgnoreCase(criteria.getSortOrder());
        return strategy.sort(matchingTasks, ascending);
    }
}
