package domain;

public class User {
    private final String id;
    private final String name;
    private final String email;
    private final UserRole role;

    public User(String id, String name, String email, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }

    @Override
    public String toString() {
        return "User[" + id + " | " + name + " (" + role + ")]";
    }
}
