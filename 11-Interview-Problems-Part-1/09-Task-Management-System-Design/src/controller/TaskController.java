package controller;

import domain.Priority;
import domain.Task;
import domain.TaskSearchCriteria;
import service.TaskService;

import java.time.LocalDateTime;
import java.util.List;

public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    public Task createTask(int id, String title, String description, LocalDateTime dueDate, Priority priority, int creatorId) {
        return taskService.createTask(id, title, description, dueDate, priority, creatorId);
    }

    public Task addSubtask(int parentTaskId, int subtaskId, String title, String description, LocalDateTime dueDate, Priority priority, int creatorId) {
        return taskService.addSubtask(parentTaskId, subtaskId, title, description, dueDate, priority, creatorId);
    }

    public void deleteTask(int taskId) {
        taskService.deleteTask(taskId);
    }

    public List<Task> searchTasks(TaskSearchCriteria criteria) {
        return taskService.searchTasks(criteria);
    }
}
