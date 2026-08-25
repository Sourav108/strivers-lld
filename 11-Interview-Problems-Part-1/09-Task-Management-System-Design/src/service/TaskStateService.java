package service;

import domain.ChangeType;
import domain.Task;
import domain.TaskStatus;
import repository.TaskRepository;

public class TaskStateService {
    private final TaskRepository taskRepository;
    private final TaskNotificationService notificationService;

    public TaskStateService(TaskRepository taskRepository, TaskNotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    public synchronized void updateTaskStatus(int taskId, TaskStatus newStatus, int updaterUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task #" + taskId + " not found."));

        String oldStatus = task.getStatus().name();
        task.updateStatus(newStatus); // State Pattern transition validation
        taskRepository.save(task);

        notificationService.logChange(taskId, updaterUserId, ChangeType.STATUS_CHANGED, oldStatus, newStatus.name());
        System.out.println("🔄 [Status Updated] Task #" + taskId + " state changed from " + oldStatus + " to " + newStatus);
    }
}
