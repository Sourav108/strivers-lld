package service;

import domain.ChangeType;
import domain.Observer.TaskSubscriber;
import domain.Task;
import domain.TaskChangeLog;
import domain.TaskSubscription;
import repository.TaskChangeLogRepository;
import repository.TaskRepository;
import repository.TaskSubscriptionRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskNotificationService {
    private final TaskRepository taskRepository;
    private final TaskChangeLogRepository changeLogRepository;
    private final TaskSubscriptionRepository subscriptionRepository;
    private final AtomicInteger logIdGenerator = new AtomicInteger(1);
    private final AtomicInteger subIdGenerator = new AtomicInteger(1);

    public TaskNotificationService(TaskRepository taskRepository,
                                   TaskChangeLogRepository changeLogRepository,
                                   TaskSubscriptionRepository subscriptionRepository) {
        this.taskRepository = taskRepository;
        this.changeLogRepository = changeLogRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public void subscribeToTask(int taskId, int userId, TaskSubscriber subscriber) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task #" + taskId + " not found."));

        task.attach(subscriber);
        subscriptionRepository.save(new TaskSubscription(subIdGenerator.getAndIncrement(), userId, taskId));
        System.out.println("🔔 [Subscribed] User #" + userId + " (" + subscriber.getSubscriberName() + ") subscribed to Task #" + taskId);
    }

    public void logChange(int taskId, int userId, ChangeType changeType, String oldValue, String newValue) {
        TaskChangeLog log = new TaskChangeLog(logIdGenerator.getAndIncrement(), taskId, userId, changeType, oldValue, newValue);
        changeLogRepository.save(log);
    }

    public List<TaskChangeLog> getTaskHistory(int taskId) {
        return changeLogRepository.findByTaskId(taskId);
    }
}
