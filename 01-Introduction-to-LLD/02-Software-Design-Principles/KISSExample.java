/**
 * KISS Principle: Keep It Simple, Stupid
 * 
 * Core Concept: Choose the simplest solution that works. Avoid convoluted,
 * overly clever, or deeply nested conditional logic.
 */

// ==========================================
// ❌ BAD DESIGN (Violates KISS: Over-engineered and verbose)
// ==========================================
class BadDeliveryValidator {
    // Unnecessarily complex boolean checks with redundant flag and branches
    public static boolean isEligibleForFreeDelivery(double orderAmount, boolean isPrimeMember) {
        boolean eligible = false;
        if (isPrimeMember == true) {
            if (orderAmount > 0) {
                eligible = true;
            } else {
                eligible = false;
            }
        } else {
            if (orderAmount >= 500) {
                eligible = true;
            } else {
                eligible = false;
            }
        }
        return eligible;
    }
}

// ==========================================
// ✅ GOOD DESIGN (Adheres to KISS: Clean, direct, and readable)
// ==========================================
// Class: Validates delivery eligibility with a clean, one-expression check.
class DeliveryValidator {
    public static boolean isEligibleForFreeDelivery(double orderAmount, boolean isPrimeMember) {
        return isPrimeMember || orderAmount >= 500;
    }
}

public class KISSExample {
    public static void main(String[] args) {
        System.out.println("Prime Member ($100): " + DeliveryValidator.isEligibleForFreeDelivery(100, true));
        System.out.println("Non-Prime ($300): " + DeliveryValidator.isEligibleForFreeDelivery(300, false));
        System.out.println("Non-Prime ($600): " + DeliveryValidator.isEligibleForFreeDelivery(600, false));
    }
}
