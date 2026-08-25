package domain.state;

import domain.TaskStatus;

public class InProgressState implements TaskState {
    @Override
    public boolean canTransitionTo(TaskStatus newStatus) {
        return newStatus == TaskStatus.REVIEW || newStatus == TaskStatus.CANCELLED || newStatus == TaskStatus.TODO;
    }

    @Override public String getStateName() { return "IN_PROGRESS"; }
    @Override public TaskStatus getStatus() { return TaskStatus.IN_PROGRESS; }
}
