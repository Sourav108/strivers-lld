package domain;

public class RefundDecision {
    private final boolean isRefundable;
    private final int refundPercentage;
    private final long refundAmountMinor;
    private final String reason;

    public RefundDecision(boolean isRefundable, int refundPercentage, long refundAmountMinor, String reason) {
        this.isRefundable = isRefundable;
        this.refundPercentage = refundPercentage;
        this.refundAmountMinor = refundAmountMinor;
        this.reason = reason;
    }

    public boolean isRefundable() { return isRefundable; }
    public int getRefundPercentage() { return refundPercentage; }
    public long getRefundAmountMinor() { return refundAmountMinor; }
    public double getRefundAmountRupees() { return refundAmountMinor / 100.0; }
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return "RefundDecision[Refundable: " + isRefundable + " | " + refundPercentage + "% (₹" + (refundAmountMinor / 100.0) + ") | Reason: '" + reason + "']";
    }
}
