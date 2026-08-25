package service;

import domain.Booking;
import domain.CancellationPolicy;
import domain.RefundDecision;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PolicyService {

    public RefundDecision evaluateCancellation(Booking booking, CancellationPolicy policy, LocalDate today) {
        if (policy == null || "NON_REFUNDABLE".equalsIgnoreCase(policy.getName())) {
            return new RefundDecision(false, 0, 0, "Non-refundable booking policy.");
        }

        LocalDate checkInDate = booking.getDateRange().getCheckInDate();
        long daysUntilCheckIn = ChronoUnit.DAYS.between(today, checkInDate);
        long hoursUntilCheckIn = daysUntilCheckIn * 24;

        if (hoursUntilCheckIn >= policy.getCutoffHoursBeforeCheckIn()) {
            long refundAmount = (booking.getTotalAmountMinor() * policy.getRefundPercent()) / 100;
            return new RefundDecision(
                    true,
                    policy.getRefundPercent(),
                    refundAmount,
                    "Cancelled " + daysUntilCheckIn + " days before check-in (Policy: " + policy.getName() + ")."
            );
        } else {
            return new RefundDecision(
                    false,
                    0,
                    0,
                    "Cancellation requested after cutoff window (" + policy.getCutoffHoursBeforeCheckIn() + " hours before check-in)."
            );
        }
    }
}
