package domain.state;

import domain.TaskStatus;

public interface TaskState {
    boolean canTransitionTo(TaskStatus newStatus);
    String getStateName();
    TaskStatus getStatus();
}
