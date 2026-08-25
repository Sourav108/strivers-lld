package domain.state;

import domain.TaskStatus;

public class CancelledState implements TaskState {
    @Override
    public boolean canTransitionTo(TaskStatus newStatus) {
        return newStatus == TaskStatus.TODO; // Reactivate
    }

    @Override public String getStateName() { return "CANCELLED"; }
    @Override public TaskStatus getStatus() { return TaskStatus.CANCELLED; }
}
