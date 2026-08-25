/**
 * Behavioural Design Patterns: State Pattern
 * 
 * Core Concept: Encapsulates state-specific behavior into separate classes
 * and delegates execution to the current active state object, allowing an object
 * to dynamically alter its behavior as its state changes.
 */

// =========================================================================
// 1. STATE INTERFACE
// =========================================================================

interface OrderState {
    void next(OrderContext context);
    void cancel(OrderContext context);
    String getStateName();
}

// =========================================================================
// 2. CONTEXT CLASS (Maintains active state and delegates actions)
// =========================================================================

class OrderContext {
    private OrderState currentState;
    private final String orderId;

    public OrderContext(String orderId) {
        this.orderId = orderId;
        this.currentState = new OrderPlacedState(); // Default initial state
    }

    public void setState(OrderState state) {
        this.currentState = state;
    }

    public void next() {
        currentState.next(this);
    }

    public void cancel() {
        currentState.cancel(this);
    }

    public String getCurrentState() {
        return currentState.getStateName();
    }

    public String getOrderId() {
        return orderId;
    }
}

// =========================================================================
// 3. CONCRETE STATE IMPLEMENTATIONS (State Machine Transitions)
// =========================================================================

// State 1: Order Placed
class OrderPlacedState implements OrderState {
    @Override
    public void next(OrderContext context) {
        context.setState(new PreparingState());
        System.out.println("👨‍🍳 [Order #" + context.getOrderId() + "] Restaurant accepted order. Status moved to: PREPARING.");
    }

    @Override
    public void cancel(OrderContext context) {
        context.setState(new CancelledState());
        System.out.println("❌ [Order #" + context.getOrderId() + "] Order successfully cancelled before kitchen prep.");
    }

    @Override
    public String getStateName() {
        return "ORDER_PLACED";
    }
}

// State 2: Preparing Food in Kitchen
class PreparingState implements OrderState {
    @Override
    public void next(OrderContext context) {
        context.setState(new OutForDeliveryState());
        System.out.println("🛵 [Order #" + context.getOrderId() + "] Delivery partner picked up food. Status moved to: OUT_FOR_DELIVERY.");
    }

    @Override
    public void cancel(OrderContext context) {
        context.setState(new CancelledState());
        System.out.println("❌ [Order #" + context.getOrderId() + "] Order cancelled with kitchen restocking fee.");
    }

    @Override
    public String getStateName() {
        return "PREPARING";
    }
}

// State 3: Out For Delivery (Disallows Cancellation)
class OutForDeliveryState implements OrderState {
    @Override
    public void next(OrderContext context) {
        context.setState(new DeliveredState());
        System.out.println("🎉 [Order #" + context.getOrderId() + "] Food successfully delivered to customer doorstep!");
    }

    @Override
    public void cancel(OrderContext context) {
        System.out.println("⛔ [Order #" + context.getOrderId() + "] Cannot cancel! Delivery driver is already en route.");
    }

    @Override
    public String getStateName() {
        return "OUT_FOR_DELIVERY";
    }
}

// State 4: Delivered (Terminal Successful State)
class DeliveredState implements OrderState {
    @Override
    public void next(OrderContext context) {
        System.out.println("ℹ️ [Order #" + context.getOrderId() + "] Order is already completed and delivered. No next state.");
    }

    @Override
    public void cancel(OrderContext context) {
        System.out.println("⛔ [Order #" + context.getOrderId() + "] Cannot cancel a delivered order. Contact customer support for returns.");
    }

    @Override
    public String getStateName() {
        return "DELIVERED";
    }
}

// State 5: Cancelled (Terminal Failed State)
class CancelledState implements OrderState {
    @Override
    public void next(OrderContext context) {
        System.out.println("⚠️ [Order #" + context.getOrderId() + "] Cancelled order cannot progress further.");
    }

    @Override
    public void cancel(OrderContext context) {
        System.out.println("⚠️ [Order #" + context.getOrderId() + "] Order is already cancelled.");
    }

    @Override
    public String getStateName() {
        return "CANCELLED";
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class StatePatternExample {
    public static void main(String[] args) {
        System.out.println("=== 🍕 Swiggy Order Lifecycle with State Pattern ===");

        // --- Scenario 1: Normal Happy Path Order Lifecycle ---
        System.out.println("\n--- Scenario 1: Successful Delivery Flow ---");
        OrderContext order1 = new OrderContext("SWIGGY-101");
        System.out.println("Initial State: " + order1.getCurrentState());

        order1.next(); // ORDER_PLACED -> PREPARING
        order1.next(); // PREPARING -> OUT_FOR_DELIVERY

        // Customer attempts to cancel while food is on the road
        System.out.println("\n[Customer clicks 'Cancel Order']");
        order1.cancel(); // Disallowed by OutForDeliveryState!

        order1.next(); // OUT_FOR_DELIVERY -> DELIVERED
        System.out.println("Final State: " + order1.getCurrentState());

        // --- Scenario 2: Early Cancellation Flow ---
        System.out.println("\n--- Scenario 2: Immediate Cancellation Flow ---");
        OrderContext order2 = new OrderContext("SWIGGY-102");
        System.out.println("Initial State: " + order2.getCurrentState());
        order2.cancel(); // Allowed in ORDER_PLACED state -> CANCELLED
        order2.next();   // Rejected by CancelledState
        System.out.println("Final State: " + order2.getCurrentState());
    }
}
