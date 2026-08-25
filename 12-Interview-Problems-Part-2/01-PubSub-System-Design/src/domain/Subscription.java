package domain;

public class Subscription {
    private final String id;
    private final String topicId;
    private final String subscriberId;
    private boolean isActive;
    private final long createdAt;

    public Subscription(String id, String topicId, String subscriberId) {
        this.id = id;
        this.topicId = topicId;
        this.subscriberId = subscriberId;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getTopicId() { return topicId; }
    public String getSubscriberId() { return subscriberId; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public long getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Subscription[" + id + " | Topic: " + topicId + " -> Sub: " + subscriberId + " (Active: " + isActive + ")]";
    }
}
