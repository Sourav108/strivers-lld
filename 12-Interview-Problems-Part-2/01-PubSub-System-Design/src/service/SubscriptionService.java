package service;

import domain.Subscriber;
import domain.Subscription;
import domain.Topic;
import domain.observer.EmailSubscriber;
import domain.observer.RealtimeSubscriber;
import repository.SubscriberRepository;
import repository.SubscriptionRepository;
import repository.TopicRepository;

import java.util.UUID;

public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final TopicRepository topicRepository;
    private final SubscriberRepository subscriberRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               TopicRepository topicRepository,
                               SubscriberRepository subscriberRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.topicRepository = topicRepository;
        this.subscriberRepository = subscriberRepository;
    }

    public synchronized Subscription subscribeToTopic(String topicId, String subscriberId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic #" + topicId + " not found."));

        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new IllegalArgumentException("Subscriber #" + subscriberId + " not found."));

        String subId = "SUB-REC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Subscription subscription = new Subscription(subId, topicId, subscriberId);
        subscriptionRepository.save(subscription);

        // 1. Always attach Email channel observer
        topic.getMessageSubject().addEmailSubscriber(new EmailSubscriber(subscriberId, subscriber.getEmail()));

        // 2. If subscriber is currently online, attach Realtime channel observer
        if (subscriber.isOnline() && subscriber.getRealtimeConnectionId() != null) {
            topic.getMessageSubject().addRealtimeSubscriber(new RealtimeSubscriber(subscriberId, subscriber.getRealtimeConnectionId()));
        }

        System.out.println("🔗 [Subscribed] " + subscriber.getEmail() + " subscribed to Topic '" + topic.getName() + "'");
        return subscription;
    }

    public synchronized void unsubscribeFromTopic(String topicId, String subscriberId) {
        subscriptionRepository.deactivateSubscription(topicId, subscriberId);
        topicRepository.findById(topicId).ifPresent(topic -> {
            topic.getMessageSubject().removeEmailSubscriber(subscriberId);
            topic.getMessageSubject().removeRealtimeSubscriber(subscriberId);
        });
        System.out.println("🔓 [Unsubscribed] Subscriber #" + subscriberId + " unsubscribed from Topic #" + topicId);
    }
}
