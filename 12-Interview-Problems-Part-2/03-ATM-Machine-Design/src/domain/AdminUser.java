package domain;

public class AdminUser {
    private final String id;
    private final String name;
    private final String pinHash;
    private boolean isActive;

    public AdminUser(String id, String name, String pinHash) {
        this.id = id;
        this.name = name;
        this.pinHash = pinHash;
        this.isActive = true;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPinHash() { return pinHash; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean validatePin(String pin) {
        return pin != null && pin.equals(pinHash);
    }
}
