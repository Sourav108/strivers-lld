package controller;

import domain.Subscription;
import service.SubscriptionService;

public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public Subscription subscribeToTopic(String topicId, String subscriberId) {
        return subscriptionService.subscribeToTopic(topicId, subscriberId);
    }

    public void unsubscribeFromTopic(String topicId, String subscriberId) {
        subscriptionService.unsubscribeFromTopic(topicId, subscriberId);
    }
}
