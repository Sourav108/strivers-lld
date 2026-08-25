package service;

import domain.DeliveryChannel;
import domain.DeliveryStatus;
import domain.Message;
import domain.MessageDelivery;
import domain.Subscriber;
import domain.Subscription;
import domain.Topic;
import repository.MessageDeliveryRepository;
import repository.MessageRepository;
import repository.SubscriberRepository;
import repository.SubscriptionRepository;
import repository.TopicRepository;

import java.util.List;
import java.util.UUID;

public class PublisherService {
    private final TopicRepository topicRepository;
    private final MessageRepository messageRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriberRepository subscriberRepository;
    private final MessageDeliveryRepository deliveryRepository;

    public PublisherService(TopicRepository topicRepository,
                            MessageRepository messageRepository,
                            SubscriptionRepository subscriptionRepository,
                            SubscriberRepository subscriberRepository,
                            MessageDeliveryRepository deliveryRepository) {
        this.topicRepository = topicRepository;
        this.messageRepository = messageRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriberRepository = subscriberRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public synchronized Message publishMessage(String topicId, String content) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic #" + topicId + " not found."));

        if (!topic.isActive()) {
            throw new IllegalStateException("❌ Cannot publish message to deactivated Topic #" + topicId);
        }

        String msgId = "MSG-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Message message = new Message(msgId, topicId, content);
        messageRepository.save(message);

        System.out.println("\n🚀 [Message Published] Topic: '" + topic.getName() + "' | Content: \"" + content + "\"");

        // Record deliveries per subscriber across channels
        List<Subscription> subscriptions = subscriptionRepository.findByTopic(topicId);
        for (Subscription sub : subscriptions) {
            String subscriberId = sub.getSubscriberId();
            Subscriber subscriber = subscriberRepository.findById(subscriberId).orElse(null);

            if (subscriber != null) {
                // 1. Email delivery: always DELIVERED
                String emailDeliveryId = "DEL-EML-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                deliveryRepository.save(new MessageDelivery(emailDeliveryId, msgId, subscriberId, DeliveryChannel.EMAIL, DeliveryStatus.DELIVERED));

                // 2. Realtime delivery: DELIVERED if online, else PENDING (queued)
                String rtDeliveryId = "DEL-RT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                if (subscriber.isOnline()) {
                    deliveryRepository.save(new MessageDelivery(rtDeliveryId, msgId, subscriberId, DeliveryChannel.REALTIME, DeliveryStatus.DELIVERED));
                } else {
                    deliveryRepository.save(new MessageDelivery(rtDeliveryId, msgId, subscriberId, DeliveryChannel.REALTIME, DeliveryStatus.PENDING));
                }
            }
        }

        // Notify observers via Observer pattern
        topic.getMessageSubject().notify(message);

        return message;
    }
}
