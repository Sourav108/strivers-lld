package domain.strategy;

import domain.Task;
import java.util.List;

public interface TaskSortingStrategy {
    List<Task> sort(List<Task> tasks, boolean ascending);
    String getStrategyName();
}
