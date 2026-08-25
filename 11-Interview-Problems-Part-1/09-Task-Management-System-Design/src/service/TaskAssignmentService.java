package service;

import domain.ChangeType;
import domain.Task;
import domain.User;
import repository.TaskRepository;
import repository.UserRepository;

public class TaskAssignmentService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskNotificationService notificationService;

    public TaskAssignmentService(TaskRepository taskRepository, UserRepository userRepository, TaskNotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public synchronized void assignTask(int taskId, int assigneeId, int modifierUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task #" + taskId + " not found."));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new IllegalArgumentException("Assignee User #" + assigneeId + " not found."));

        String oldAssignee = task.getAssigneeId() != null ? String.valueOf(task.getAssigneeId()) : "UNASSIGNED";
        task.setAssigneeId(assigneeId);
        taskRepository.save(task);

        notificationService.logChange(taskId, modifierUserId, ChangeType.ASSIGNED, oldAssignee, assignee.getUsername());
        System.out.println("👤 [Task Assigned] Task #" + taskId + " assigned to User: " + assignee.getUsername());
    }
}
