package domain.state;

import domain.TaskStatus;

public class CompletedState implements TaskState {
    @Override
    public boolean canTransitionTo(TaskStatus newStatus) {
        return newStatus == TaskStatus.IN_PROGRESS; // Reopen
    }

    @Override public String getStateName() { return "COMPLETED"; }
    @Override public TaskStatus getStatus() { return TaskStatus.COMPLETED; }
}
