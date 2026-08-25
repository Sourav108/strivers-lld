package domain;

public class MessageDelivery {
    private final String id;
    private final String messageId;
    private final String subscriberId;
    private final DeliveryChannel channel;
    private DeliveryStatus status;
    private final long createdAt;
    private Long acknowledgedAt;

    public MessageDelivery(String id, String messageId, String subscriberId, DeliveryChannel channel, DeliveryStatus status) {
        this.id = id;
        this.messageId = messageId;
        this.subscriberId = subscriberId;
        this.channel = channel;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getMessageId() { return messageId; }
    public String getSubscriberId() { return subscriberId; }
    public DeliveryChannel getChannel() { return channel; }
    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }
    public long getCreatedAt() { return createdAt; }
    public Long getAcknowledgedAt() { return acknowledgedAt; }

    public void acknowledge() {
        this.status = DeliveryStatus.ACKNOWLEDGED;
        this.acknowledgedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Delivery[" + id + " | Msg: " + messageId + " | Sub: " + subscriberId + " | " + channel + " -> " + status + "]";
    }
}
