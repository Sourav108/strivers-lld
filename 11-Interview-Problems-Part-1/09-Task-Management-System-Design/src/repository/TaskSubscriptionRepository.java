package repository;

import domain.TaskSubscription;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TaskSubscriptionRepository {
    private final Map<Integer, TaskSubscription> subscriptions = new ConcurrentHashMap<>();

    public TaskSubscription save(TaskSubscription subscription) {
        subscriptions.put(subscription.getId(), subscription);
        return subscription;
    }

    public List<TaskSubscription> findByTaskId(int taskId) {
        return subscriptions.values().stream()
                .filter(s -> s.getTaskId() == taskId && s.isActive())
                .collect(Collectors.toList());
    }

    public List<TaskSubscription> findByUserId(int userId) {
        return subscriptions.values().stream()
                .filter(s -> s.getUserId() == userId && s.isActive())
                .collect(Collectors.toList());
    }
}
