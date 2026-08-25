package repository.impl;

import domain.Subscriber;
import repository.SubscriberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SubscriberRepositoryImpl implements SubscriberRepository {
    private final Map<String, Subscriber> subscribers = new ConcurrentHashMap<>();

    @Override
    public Subscriber save(Subscriber subscriber) {
        subscribers.put(subscriber.getId(), subscriber);
        return subscriber;
    }

    @Override
    public Optional<Subscriber> findById(String subscriberId) {
        return Optional.ofNullable(subscribers.get(subscriberId));
    }

    @Override
    public List<Subscriber> findAll() {
        return new ArrayList<>(subscribers.values());
    }

    @Override
    public void updateOnlineStatus(String subscriberId, boolean isOnline, String connectionId) {
        Subscriber subscriber = subscribers.get(subscriberId);
        if (subscriber != null) {
            subscriber.setOnline(isOnline, connectionId);
        }
    }

    @Override
    public void deleteById(String subscriberId) {
        subscribers.remove(subscriberId);
    }
}
