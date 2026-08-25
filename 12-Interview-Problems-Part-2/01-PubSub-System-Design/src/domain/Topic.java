package domain;

import domain.observer.MessageSubject;

public class Topic {
    private final String id;
    private final String name;
    private boolean isActive;
    private final long createdAt;
    private final MessageSubject messageSubject;

    public Topic(String id, String name) {
        this.id = id;
        this.name = name;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
        this.messageSubject = new MessageSubject();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public long getCreatedAt() { return createdAt; }
    public MessageSubject getMessageSubject() { return messageSubject; }

    @Override
    public String toString() {
        return "Topic[" + id + " | '" + name + "' | Active: " + isActive + "]";
    }
}
