package domain.observer;

import domain.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageSubject {
    private final List<SubscriberObserver> emailSubscribers = new CopyOnWriteArrayList<>();
    private final List<SubscriberObserver> realtimeSubscribers = new CopyOnWriteArrayList<>();

    public void addEmailSubscriber(SubscriberObserver subscriber) {
        emailSubscribers.add(subscriber);
    }

    public void removeEmailSubscriber(String subscriberId) {
        emailSubscribers.removeIf(s -> s.getSubscriberId().equals(subscriberId));
    }

    public void addRealtimeSubscriber(SubscriberObserver subscriber) {
        // Prevent duplicate realtime connections for the same subscriber
        removeRealtimeSubscriber(subscriber.getSubscriberId());
        realtimeSubscribers.add(subscriber);
    }

    public void removeRealtimeSubscriber(String subscriberId) {
        realtimeSubscribers.removeIf(s -> s.getSubscriberId().equals(subscriberId));
    }

    public void notify(Message message) {
        notifyEmailSubscribers(message);
        notifyRealtimeSubscribers(message);
    }

    public void notifyEmailSubscribers(Message message) {
        for (SubscriberObserver sub : emailSubscribers) {
            sub.update(message);
        }
    }

    public void notifyRealtimeSubscribers(Message message) {
        for (SubscriberObserver sub : realtimeSubscribers) {
            sub.update(message);
        }
    }

    public List<SubscriberObserver> getEmailSubscribers() {
        return new ArrayList<>(emailSubscribers);
    }

    public List<SubscriberObserver> getRealtimeSubscribers() {
        return new ArrayList<>(realtimeSubscribers);
    }
}
