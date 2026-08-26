package domain;

import java.time.LocalDateTime;

public class Rider {
    private final int id;
    private final String username;
    private final String email;
    private final String phoneNumber;
    private final String name;
    private final LocalDateTime createdAt;

    public Rider(int id, String username, String email, String phoneNumber, String name) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Rider[ID=" + id + ", Name=" + name + ", Phone=" + phoneNumber + "]";
    }
}
