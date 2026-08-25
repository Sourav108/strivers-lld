package domain.state;

import domain.TaskStatus;

public class ReviewState implements TaskState {
    @Override
    public boolean canTransitionTo(TaskStatus newStatus) {
        return newStatus == TaskStatus.COMPLETED || newStatus == TaskStatus.IN_PROGRESS;
    }

    @Override public String getStateName() { return "REVIEW"; }
    @Override public TaskStatus getStatus() { return TaskStatus.REVIEW; }
}
