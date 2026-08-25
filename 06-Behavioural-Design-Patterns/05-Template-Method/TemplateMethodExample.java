/**
 * Behavioural Design Patterns: Template Method Pattern
 * 
 * Core Concept: Defines the invariant skeleton of an algorithm in a base class
 * method (marked final), deferring specific customizable steps to subclasses.
 */

// =========================================================================
// 1. ABSTRACT BASE CLASS (Defines the Template Method & Common Invariant Steps)
// =========================================================================

abstract class NotificationSender {

    // 🔒 The Template Method: Final to prevent subclasses from altering the sequence
    public final void send(String to, String rawMessage) {
        System.out.println("\n-------------------------------------------------");
        System.out.println("🚀 Initiating notification pipeline for: " + to);
        System.out.println("-------------------------------------------------");

        // 1. Invariant Step: Rate Limiting
        rateLimitCheck(to);

        // 2. Invariant Step: Recipient Validation
        validateRecipient(to);

        // 3. Invariant Step: Common Message Sanitization
        String formatted = formatMessage(rawMessage);

        // 4. Invariant Step: Pre-send Audit Logging
        preSendAuditLog(to, formatted);

        // 5. Primitive Operation: Subclass specific message formatting/composition
        String composedMessage = composeMessage(formatted);

        // 6. Primitive Operation: Subclass specific protocol transmission
        sendMessage(to, composedMessage);

        // 7. Hook: Optional post-send analytics (can be overridden by subclasses)
        postSendAnalytics(to);
    }

    // Invariant Concrete Operation 1
    private void rateLimitCheck(String to) {
        System.out.println("1️⃣ [RateLimiter] Verified quota for recipient: " + to);
    }

    // Invariant Concrete Operation 2
    private void validateRecipient(String to) {
        System.out.println("2️⃣ [Validator] Recipient format verified: " + to);
    }

    // Invariant Concrete Operation 3
    private String formatMessage(String message) {
        String trimmed = message.trim();
        System.out.println("3️⃣ [Sanitizer] Message sanitized and trimmed: '" + trimmed + "'");
        return trimmed;
    }

    // Invariant Concrete Operation 4
    private void preSendAuditLog(String to, String formatted) {
        System.out.println("4️⃣ [AuditLog] Stored pre-send intent record for: " + to);
    }

    // Abstract Primitive Operations (Mandatory for subclasses to implement)
    protected abstract String composeMessage(String formattedMessage);
    protected abstract void sendMessage(String to, String message);

    // Optional Hook with default implementation
    protected void postSendAnalytics(String to) {
        System.out.println("7️⃣ [Analytics Hook] Standard global analytics counter incremented for " + to);
    }
}

// =========================================================================
// 2. CONCRETE SUBCLASSES (Implementing specific Primitive Operations)
// =========================================================================

class EmailNotification extends NotificationSender {

    @Override
    protected String composeMessage(String formattedMessage) {
        System.out.println("5️⃣ [Email Composer] Wrapping in responsive HTML email template.");
        return "<html><body><h2>System Notice</h2><p>" + formattedMessage + "</p></body></html>";
    }

    @Override
    protected void sendMessage(String to, String message) {
        System.out.println("6️⃣ [SMTP Gateway] Dispatching TLS email to " + to + " with payload:\n    " + message);
    }
}

class SMSNotification extends NotificationSender {

    @Override
    protected String composeMessage(String formattedMessage) {
        System.out.println("5️⃣ [SMS Composer] Truncating to 160 GSM chars and prepending OTP header.");
        return "[TUF-AUTH-OTP] " + formattedMessage;
    }

    @Override
    protected void sendMessage(String to, String message) {
        System.out.println("6️⃣ [Telco SMS Gateway] Sending cellular SMS to " + to + ": '" + message + "'");
    }

    // Overriding optional hook for custom SMS delivery receipts
    @Override
    protected void postSendAnalytics(String to) {
        System.out.println("7️⃣ [Custom SMS Hook] Recording telecom carrier delivery receipt & billing charge for " + to);
    }
}

class PushNotification extends NotificationSender {

    @Override
    protected String composeMessage(String formattedMessage) {
        System.out.println("5️⃣ [FCM Push Composer] Packaging into APNS/FCM JSON payload.");
        return "{\"notification\": {\"body\": \"" + formattedMessage + "\"}}";
    }

    @Override
    protected void sendMessage(String to, String message) {
        System.out.println("6️⃣ [FCM / Apple APNS] Pushing mobile notification token " + to + ": " + message);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class TemplateMethodExample {
    public static void main(String[] args) {
        System.out.println("=== 📬 Notification Delivery Service (Template Method Pattern) ===");

        // 1. Send HTML Email
        NotificationSender emailSender = new EmailNotification();
        emailSender.send("sourav@takeuforward.org", "Welcome to Striver's LLD Course!");

        // 2. Send Cellular SMS (Overrides Analytics Hook)
        NotificationSender smsSender = new SMSNotification();
        smsSender.send("+91-9876543210", "Your login verification code is 849201.");

        // 3. Send Mobile App Push Notification
        NotificationSender pushSender = new PushNotification();
        pushSender.send("DEVICE_TOKEN_AF932", "New assignment posted in System Design!");
    }
}
