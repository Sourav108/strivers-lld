/**
 * Behavioural Design Patterns: Chain of Responsibility Pattern
 * 
 * Core Concept: Passes a request along a chain of sequential handlers.
 * Each handler either processes the request or delegates it to the next handler.
 */

// =========================================================================
// 1. ABSTRACT HANDLER (Defines chain link and delegation contract)
// =========================================================================

abstract class SupportHandler {
    protected SupportHandler nextHandler;

    // Fluent builder pattern for linking handlers
    public SupportHandler setNextHandler(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public abstract void handleRequest(String requestType, String message);
}

// =========================================================================
// 2. CONCRETE HANDLERS (Department-specific processors)
// =========================================================================

class GeneralSupport extends SupportHandler {
    @Override
    public void handleRequest(String requestType, String message) {
        if ("GENERAL".equalsIgnoreCase(requestType)) {
            System.out.println("🙋 [General Helpdesk] Handled inquiry: '" + message + "'");
        } else if (nextHandler != null) {
            System.out.println("⏩ [General Helpdesk] Cannot handle '" + requestType + "'. Forwarding to next department...");
            nextHandler.handleRequest(requestType, message);
        }
    }
}

class BillingSupport extends SupportHandler {
    @Override
    public void handleRequest(String requestType, String message) {
        if ("REFUND".equalsIgnoreCase(requestType) || "BILLING".equalsIgnoreCase(requestType)) {
            System.out.println("💳 [Billing & Finance] Processed transaction/refund: '" + message + "'");
        } else if (nextHandler != null) {
            System.out.println("⏩ [Billing & Finance] Cannot handle '" + requestType + "'. Forwarding to next department...");
            nextHandler.handleRequest(requestType, message);
        }
    }
}

class TechnicalSupport extends SupportHandler {
    @Override
    public void handleRequest(String requestType, String message) {
        if ("TECHNICAL".equalsIgnoreCase(requestType) || "BUG".equalsIgnoreCase(requestType)) {
            System.out.println("💻 [Tech Support Engineer] Debugging & resolving issue: '" + message + "'");
        } else if (nextHandler != null) {
            System.out.println("⏩ [Tech Support] Cannot handle '" + requestType + "'. Forwarding to next department...");
            nextHandler.handleRequest(requestType, message);
        }
    }
}

class DeliverySupport extends SupportHandler {
    @Override
    public void handleRequest(String requestType, String message) {
        if ("DELIVERY".equalsIgnoreCase(requestType) || "SHIPPING".equalsIgnoreCase(requestType)) {
            System.out.println("🚚 [Logistics Operations] Tracking and rerouting package: '" + message + "'");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(requestType, message);
        } else {
            System.out.println("❌ [Unresolved] No department found in the escalation chain to handle request type: '" + requestType + "'");
        }
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class ChainOfResponsibilityExample {
    public static void main(String[] args) {
        System.out.println("=== 🎧 Customer Support Ticket Routing with Chain of Responsibility ===");

        // 1. Instantiate individual handlers
        SupportHandler general = new GeneralSupport();
        SupportHandler billing = new BillingSupport();
        SupportHandler technical = new TechnicalSupport();
        SupportHandler delivery = new DeliverySupport();

        // 2. Build the processing chain: General -> Billing -> Technical -> Delivery
        general.setNextHandler(billing)
               .setNextHandler(technical)
               .setNextHandler(delivery);

        // 3. Test various support tickets sent to the front of the chain
        System.out.println("\n--- Ticket 1: General Query ---");
        general.handleRequest("GENERAL", "What are the holiday return policies?");

        System.out.println("\n--- Ticket 2: Billing / Refund ---");
        general.handleRequest("REFUND", "Charged twice for order #9821.");

        System.out.println("\n--- Ticket 3: Technical Bug ---");
        general.handleRequest("TECHNICAL", "App crashes with 500 error when clicking checkout button.");

        System.out.println("\n--- Ticket 4: Delayed Package ---");
        general.handleRequest("DELIVERY", "Courier was out for delivery 3 days ago but never arrived.");

        System.out.println("\n--- Ticket 5: Unknown Request Type ---");
        general.handleRequest("LEGAL_LAWSUIT", "Subpoena document regarding trademark dispute.");
    }
}
