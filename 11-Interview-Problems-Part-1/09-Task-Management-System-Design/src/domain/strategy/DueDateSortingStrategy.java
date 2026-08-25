package domain.strategy;

import domain.Task;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DueDateSortingStrategy implements TaskSortingStrategy {
    @Override
    public List<Task> sort(List<Task> tasks, boolean ascending) {
        List<Task> sorted = new ArrayList<>(tasks);
        Comparator<Task> comparator = Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));
        if (!ascending) {
            comparator = comparator.reversed();
        }
        sorted.sort(comparator);
        return sorted;
    }

    @Override
    public String getStrategyName() {
        return "DUE_DATE";
    }
}
