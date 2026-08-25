package domain;

public enum Priority {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4);

    private final int level;

    Priority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
