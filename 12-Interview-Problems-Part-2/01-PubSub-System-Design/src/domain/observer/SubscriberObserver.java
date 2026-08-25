package domain.observer;

import domain.Message;

public interface SubscriberObserver {
    void update(Message message);
    String getSubscriberId();
}
