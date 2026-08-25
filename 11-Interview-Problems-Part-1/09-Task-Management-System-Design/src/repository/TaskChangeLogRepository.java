package repository;

import domain.TaskChangeLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TaskChangeLogRepository {
    private final Map<Integer, TaskChangeLog> logs = new ConcurrentHashMap<>();

    public TaskChangeLog save(TaskChangeLog log) {
        logs.put(log.getId(), log);
        return log;
    }

    public List<TaskChangeLog> findByTaskId(int taskId) {
        return logs.values().stream()
                .filter(l -> l.getTaskId() == taskId)
                .collect(Collectors.toList());
    }
}
