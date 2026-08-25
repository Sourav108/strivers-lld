/**
 * SOLID Principles: Liskov Substitution Principle (LSP)
 * 
 * Core Concept: Subtypes must be substitutable for their base types 
 * without altering the correctness or expected behavior of the program.
 */

// =========================================================================
// ❌ BAD DESIGN (Violates LSP: The Classic Rectangle-Square Dilemma)
// =========================================================================

class BadRectangle {
    protected int width;
    protected int height;

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getArea() {
        return width * height;
    }
}

// Square alters the behavior of setWidth and setHeight, violating parent assumptions.
class BadSquare extends BadRectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // Modifies height unexpectedly!
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
        this.width = height; // Modifies width unexpectedly!
    }
}

// =========================================================================
// ✅ GOOD DESIGN 1 (Adheres to LSP: Decoupled Shape Contract)
// =========================================================================

interface Shape {
    int getArea();
}

class Rectangle implements Shape {
    private final int width;
    private final int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getArea() {
        return width * height;
    }
}

class Square implements Shape {
    private final int side;

    public Square(int side) {
        this.side = side;
    }

    @Override
    public int getArea() {
        return side * side;
    }
}

// =========================================================================
// ✅ GOOD DESIGN 2 (Adheres to LSP: Backend Notification Service)
// All notification channels conform to the Notification contract.
// =========================================================================

interface Notification {
    void send(String recipient, String message);
}

class EmailNotification implements Notification {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[Email] 📧 Sent to " + recipient + ": " + message);
    }
}

class SMSNotification implements Notification {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[SMS] 📱 Sent to " + recipient + ": " + message);
    }
}

class PushNotification implements Notification {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[Push] 🔔 Sent to " + recipient + ": " + message);
    }
}

// Notification Dispatcher can substitute ANY Notification subtype safely.
class NotificationDispatcher {
    public void dispatch(Notification notification, String recipient, String message) {
        notification.send(recipient, message);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class LSPExample {

    // Helper client function assuming standard Rectangle behavior
    private static void testRectangleArea(BadRectangle r, String shapeName) {
        r.setWidth(5);
        r.setHeight(10);
        int expectedArea = 5 * 10; // 50
        int actualArea = r.getArea();

        System.out.println("Testing " + shapeName + " (Expected: " + expectedArea + ", Actual: " + actualArea + ")");
        if (actualArea != expectedArea) {
            System.out.println("  ❌ LSP Violation Detected! Subtype altered parent expectations.");
        } else {
            System.out.println("  ✅ Behaved as expected.");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== ❌ 1. Bad Design: Rectangle-Square Substitution ===");
        BadRectangle regularRectangle = new BadRectangle();
        testRectangleArea(regularRectangle, "Regular Rectangle");

        // Substituting Square for Rectangle breaks the test!
        BadRectangle squareAsRectangle = new BadSquare();
        testRectangleArea(squareAsRectangle, "Square as Rectangle");

        System.out.println("\n=== ✅ 2. Good Design: Shape Contract ===");
        Shape rect = new Rectangle(5, 10);
        Shape sq = new Square(5);
        System.out.println("Rectangle Area: " + rect.getArea());
        System.out.println("Square Area:    " + sq.getArea());

        System.out.println("\n=== ✅ 3. Good Design: Backend Notification Substitution ===");
        NotificationDispatcher dispatcher = new NotificationDispatcher();
        
        Notification email = new EmailNotification();
        Notification sms = new SMSNotification();
        Notification push = new PushNotification();

        // Any subtype can be substituted seamlessly without breaking the dispatcher
        dispatcher.dispatch(email, "sourav@example.com", "Order #1024 Confirmed!");
        dispatcher.dispatch(sms, "+91-9876543210", "OTP: 458921");
        dispatcher.dispatch(push, "device_token_xyz", "Flash Sale starts in 5 mins!");
    }
}
