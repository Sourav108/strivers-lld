package repository;

import domain.Task;
import domain.TaskSearchCriteria;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(int id);
    void delete(int id);
    List<Task> findAll();
    List<Task> findByAssignee(int assigneeId);
    List<Task> findByParentTask(int parentTaskId);
    List<Task> search(TaskSearchCriteria criteria);
}
