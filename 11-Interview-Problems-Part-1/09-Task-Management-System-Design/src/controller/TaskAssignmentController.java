package controller;

import service.TaskAssignmentService;

public class TaskAssignmentController {
    private final TaskAssignmentService taskAssignmentService;

    public TaskAssignmentController(TaskAssignmentService taskAssignmentService) {
        this.taskAssignmentService = taskAssignmentService;
    }

    public void assignTask(int taskId, int assigneeId, int modifierUserId) {
        taskAssignmentService.assignTask(taskId, assigneeId, modifierUserId);
    }
}
