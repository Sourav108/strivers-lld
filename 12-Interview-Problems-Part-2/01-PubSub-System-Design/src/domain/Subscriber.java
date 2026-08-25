package domain;

public class Subscriber {
    private final String id;
    private final String email;
    private String realtimeConnectionId;
    private boolean isOnline;
    private final long createdAt;
    private long lastHeartbeat;

    public Subscriber(String id, String email) {
        this.id = id;
        this.email = email;
        this.isOnline = true; // default online on registration
        this.createdAt = System.currentTimeMillis();
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getRealtimeConnectionId() { return realtimeConnectionId; }
    public boolean isOnline() { return isOnline; }
    public long getCreatedAt() { return createdAt; }
    public long getLastHeartbeat() { return lastHeartbeat; }

    public void setOnline(boolean online, String connectionId) {
        this.isOnline = online;
        this.realtimeConnectionId = connectionId;
        this.lastHeartbeat = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Subscriber[" + id + " | " + email + " | Online: " + isOnline + " (" + realtimeConnectionId + ")]";
    }
}
