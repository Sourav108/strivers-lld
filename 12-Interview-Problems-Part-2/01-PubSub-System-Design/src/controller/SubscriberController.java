package controller;

import domain.Subscriber;
import service.SubscriberService;

public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    public Subscriber registerSubscriber(String email) {
        return subscriberService.registerSubscriber(email);
    }

    public void goOnline(String subscriberId, String connectionId) {
        subscriberService.goOnline(subscriberId, connectionId);
    }

    public void goOffline(String subscriberId) {
        subscriberService.goOffline(subscriberId);
    }
}
