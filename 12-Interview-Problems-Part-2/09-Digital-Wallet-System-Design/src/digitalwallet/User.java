package digitalwallet;

/**
 * Represents a registered user who owns a digital wallet.
 */
public class User {
    private final int id;
    private final String name;
    private final String email;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User[ID=" + id + ", Name=" + name + ", Email=" + email + "]";
    }
}
