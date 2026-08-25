package domain.observer;

import domain.Message;

public class RealtimeSubscriber implements SubscriberObserver {
    private final String subscriberId;
    private final String connectionId;

    public RealtimeSubscriber(String subscriberId, String connectionId) {
        this.subscriberId = subscriberId;
        this.connectionId = connectionId;
    }

    @Override
    public void update(Message message) {
        System.out.println("   ⚡ [Realtime WebSocket -> " + connectionId + " (User: " + subscriberId + ")] " + message.getContent());
    }

    @Override
    public String getSubscriberId() {
        return subscriberId;
    }

    public String getConnectionId() {
        return connectionId;
    }
}
