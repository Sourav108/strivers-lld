package domain.Observer;

import domain.ChangeType;

public interface TaskSubscriber {
    void update(int taskId, ChangeType changeType, String oldValue, String newValue);
    String getSubscriberName();
}
