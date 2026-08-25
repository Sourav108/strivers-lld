package domain;

public class CancellationPolicy {
    private final String id;
    private final String name; // NON_REFUNDABLE, PARTIAL, FLEXIBLE
    private final int refundPercent; // 0..100
    private final int cutoffHoursBeforeCheckIn;

    public CancellationPolicy(String id, String name, int refundPercent, int cutoffHoursBeforeCheckIn) {
        this.id = id;
        this.name = name;
        this.refundPercent = refundPercent;
        this.cutoffHoursBeforeCheckIn = cutoffHoursBeforeCheckIn;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getRefundPercent() { return refundPercent; }
    public int getCutoffHoursBeforeCheckIn() { return cutoffHoursBeforeCheckIn; }

    @Override
    public String toString() {
        return "CancellationPolicy[" + name + " | Refund: " + refundPercent + "% | Cutoff: " + cutoffHoursBeforeCheckIn + "h]";
    }
}
