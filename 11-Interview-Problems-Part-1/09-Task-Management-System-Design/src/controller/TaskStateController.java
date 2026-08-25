package controller;

import domain.TaskStatus;
import service.TaskStateService;

public class TaskStateController {
    private final TaskStateService taskStateService;

    public TaskStateController(TaskStateService taskStateService) {
        this.taskStateService = taskStateService;
    }

    public void updateTaskStatus(int taskId, TaskStatus newStatus, int updaterUserId) {
        taskStateService.updateTaskStatus(taskId, newStatus, updaterUserId);
    }
}
