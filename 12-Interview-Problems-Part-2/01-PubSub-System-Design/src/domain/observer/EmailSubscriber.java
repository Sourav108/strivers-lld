package domain.observer;

import domain.Message;

public class EmailSubscriber implements SubscriberObserver {
    private final String subscriberId;
    private final String email;

    public EmailSubscriber(String subscriberId, String email) {
        this.subscriberId = subscriberId;
        this.email = email;
    }

    @Override
    public void update(Message message) {
        System.out.println("   📧 [Email Dispatched -> " + email + "] " + message.getContent() + " (Topic: " + message.getTopicId() + ")");
    }

    @Override
    public String getSubscriberId() {
        return subscriberId;
    }

    public String getEmail() {
        return email;
    }
}
