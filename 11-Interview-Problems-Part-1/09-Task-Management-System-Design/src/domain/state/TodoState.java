package domain.state;

import domain.TaskStatus;

public class TodoState implements TaskState {
    @Override
    public boolean canTransitionTo(TaskStatus newStatus) {
        return newStatus == TaskStatus.IN_PROGRESS || newStatus == TaskStatus.CANCELLED;
    }

    @Override public String getStateName() { return "TODO"; }
    @Override public TaskStatus getStatus() { return TaskStatus.TODO; }
}
