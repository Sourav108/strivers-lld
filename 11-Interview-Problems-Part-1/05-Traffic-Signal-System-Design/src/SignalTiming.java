public class SignalTiming {
    private final Direction direction;
    private int greenDurationSeconds;
    private final int yellowDurationSeconds = 3; // Safety standard constant
    private boolean isDynamic;

    public SignalTiming(Direction direction, int greenDurationSeconds, boolean isDynamic) {
        this.direction = direction;
        this.greenDurationSeconds = greenDurationSeconds;
        this.isDynamic = isDynamic;
    }

    public Direction getDirection() { return direction; }
    public int getGreenDurationSeconds() { return greenDurationSeconds; }
    public int getYellowDurationSeconds() { return yellowDurationSeconds; }
    public boolean isDynamic() { return isDynamic; }

    public void setGreenDurationSeconds(int seconds) {
        this.greenDurationSeconds = Math.max(5, Math.min(seconds, 120)); // Bounded 5s to 120s
    }

    public void setDynamic(boolean dynamic) {
        isDynamic = dynamic;
    }

    @Override
    public String toString() {
        return "SignalTiming{" + direction + ": Green=" + greenDurationSeconds + "s, Yellow=" + yellowDurationSeconds + "s, Dynamic=" + isDynamic + "}";
    }
}
