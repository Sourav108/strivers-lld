package domain;

import java.time.LocalDateTime;

public class User {
    private final int id;
    private final String username;
    private final String email;
    private final String name;
    private SubscriptionTier subscriptionTier;
    private final LocalDateTime createdAt;

    public User(int id, String username, String email, String name, SubscriptionTier subscriptionTier) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.subscriptionTier = subscriptionTier;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public synchronized SubscriptionTier getSubscriptionTier() {
        return subscriptionTier;
    }

    public synchronized void setSubscriptionTier(SubscriptionTier subscriptionTier) {
        this.subscriptionTier = subscriptionTier;
    }

    public boolean isPremium() {
        return subscriptionTier == SubscriptionTier.PREMIUM;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public synchronized String toString() {
        return "User[ID=" + id + ", Name=" + name + ", Tier=" + subscriptionTier + "]";
    }
}
