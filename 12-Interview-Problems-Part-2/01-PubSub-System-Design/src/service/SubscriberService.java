package service;

import domain.DeliveryChannel;
import domain.DeliveryStatus;
import domain.Message;
import domain.MessageDelivery;
import domain.Subscriber;
import domain.Subscription;
import domain.Topic;
import domain.observer.RealtimeSubscriber;
import repository.MessageDeliveryRepository;
import repository.MessageRepository;
import repository.SubscriberRepository;
import repository.SubscriptionRepository;
import repository.TopicRepository;

import java.util.List;
import java.util.UUID;

public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TopicRepository topicRepository;
    private final MessageRepository messageRepository;
    private final MessageDeliveryRepository deliveryRepository;

    public SubscriberService(SubscriberRepository subscriberRepository,
                             SubscriptionRepository subscriptionRepository,
                             TopicRepository topicRepository,
                             MessageRepository messageRepository,
                             MessageDeliveryRepository deliveryRepository) {
        this.subscriberRepository = subscriberRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.topicRepository = topicRepository;
        this.messageRepository = messageRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public Subscriber registerSubscriber(String email) {
        String id = "SUB-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Subscriber subscriber = new Subscriber(id, email);
        subscriberRepository.save(subscriber);
        System.out.println("👤 [Subscriber Registered] " + subscriber);
        return subscriber;
    }

    public Subscriber getSubscriber(String subscriberId) {
        return subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new IllegalArgumentException("Subscriber #" + subscriberId + " not found."));
    }

    public synchronized void goOnline(String subscriberId, String connectionId) {
        Subscriber subscriber = getSubscriber(subscriberId);
        subscriber.setOnline(true, connectionId);
        subscriberRepository.save(subscriber);
        System.out.println("🟢 [Status Change -> ONLINE] Subscriber " + subscriber.getEmail() + " connected (WS: " + connectionId + ")");

        // Re-attach RealtimeSubscriber observer to all active subscriptions
        List<Subscription> subscriptions = subscriptionRepository.findBySubscriber(subscriberId);
        for (Subscription sub : subscriptions) {
            topicRepository.findById(sub.getTopicId()).ifPresent(topic -> {
                topic.getMessageSubject().addRealtimeSubscriber(new RealtimeSubscriber(subscriberId, connectionId));
            });
        }

        // Push any queued pending realtime deliveries
        pushPendingDeliveries(subscriberId);
    }

    public synchronized void goOffline(String subscriberId) {
        Subscriber subscriber = getSubscriber(subscriberId);
        subscriber.setOnline(false, null);
        subscriberRepository.save(subscriber);
        System.out.println("🔴 [Status Change -> OFFLINE] Subscriber " + subscriber.getEmail() + " disconnected.");

        // Remove RealtimeSubscriber observer from all subscribed topics
        List<Subscription> subscriptions = subscriptionRepository.findBySubscriber(subscriberId);
        for (Subscription sub : subscriptions) {
            topicRepository.findById(sub.getTopicId()).ifPresent(topic -> {
                topic.getMessageSubject().removeRealtimeSubscriber(subscriberId);
            });
        }
    }

    public synchronized void pushPendingDeliveries(String subscriberId) {
        List<MessageDelivery> pending = deliveryRepository.findPendingBySubscriber(subscriberId);
        if (!pending.isEmpty()) {
            System.out.println("   📬 [Syncing Offline Messages] Pushing " + pending.size() + " pending message(s) to Subscriber #" + subscriberId);
            for (MessageDelivery delivery : pending) {
                messageRepository.findById(delivery.getMessageId()).ifPresent(msg -> {
                    System.out.println("      ↳ 📨 [Queued Message Delivered via Realtime] " + msg.getContent());
                    delivery.setStatus(DeliveryStatus.DELIVERED);
                    deliveryRepository.save(delivery);
                });
            }
        }
    }
}
