package controller;

import domain.Observer.TaskSubscriber;
import domain.TaskChangeLog;
import service.TaskNotificationService;

import java.util.List;

public class TaskNotificationController {
    private final TaskNotificationService notificationService;

    public TaskNotificationController(TaskNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void subscribeToTask(int taskId, int userId, TaskSubscriber subscriber) {
        notificationService.subscribeToTask(taskId, userId, subscriber);
    }

    public List<TaskChangeLog> getTaskHistory(int taskId) {
        return notificationService.getTaskHistory(taskId);
    }
}
