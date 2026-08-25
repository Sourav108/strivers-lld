package domain;

public class VehicleCounter {
    private final Direction direction;
    private int count;
    private long lastUpdatedTimestamp;

    public VehicleCounter(Direction direction) {
        this.direction = direction;
        this.count = 0;
        this.lastUpdatedTimestamp = System.currentTimeMillis();
    }

    public synchronized void setCount(int count) {
        this.count = count;
        this.lastUpdatedTimestamp = System.currentTimeMillis();
    }

    public synchronized void incrementCount() {
        this.count++;
        this.lastUpdatedTimestamp = System.currentTimeMillis();
    }

    public synchronized int getCount() { return count; }
    public Direction getDirection() { return direction; }
    public long getLastUpdatedTimestamp() { return lastUpdatedTimestamp; }

    @Override
    public String toString() {
        return "[" + direction + " Vehicle Count: " + count + "]";
    }
}
