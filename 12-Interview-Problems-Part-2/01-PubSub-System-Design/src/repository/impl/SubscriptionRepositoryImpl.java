package repository.impl;

import domain.Subscription;
import repository.SubscriptionRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SubscriptionRepositoryImpl implements SubscriptionRepository {
    private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();

    @Override
    public Subscription save(Subscription subscription) {
        subscriptions.put(subscription.getId(), subscription);
        return subscription;
    }

    @Override
    public List<Subscription> findByTopic(String topicId) {
        return subscriptions.values().stream()
                .filter(s -> s.getTopicId().equals(topicId) && s.isActive())
                .collect(Collectors.toList());
    }

    @Override
    public List<Subscription> findBySubscriber(String subscriberId) {
        return subscriptions.values().stream()
                .filter(s -> s.getSubscriberId().equals(subscriberId) && s.isActive())
                .collect(Collectors.toList());
    }

    @Override
    public void deactivateSubscription(String topicId, String subscriberId) {
        for (Subscription sub : subscriptions.values()) {
            if (sub.getTopicId().equals(topicId) && sub.getSubscriberId().equals(subscriberId)) {
                sub.setActive(false);
            }
        }
    }

    @Override
    public void deleteById(String subscriptionId) {
        subscriptions.remove(subscriptionId);
    }
}
