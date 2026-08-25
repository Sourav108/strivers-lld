import java.util.ArrayList;
import java.util.List;

/**
 * Behavioural Design Patterns: Observer Pattern
 * 
 * Core Concept: Defines a one-to-many dependency between objects so that
 * when one object (Subject) changes state, all its dependents (Observers)
 * are notified and updated automatically.
 */

// =========================================================================
// 1. OBSERVER INTERFACE (Subscriber Contract)
// =========================================================================

interface Subscriber {
    void update(String channelName, String videoTitle);
}

// =========================================================================
// 2. CONCRETE OBSERVERS (Different Notification Handlers)
// =========================================================================

class EmailSubscriber implements Subscriber {
    private final String email;

    public EmailSubscriber(String email) {
        this.email = email;
    }

    @Override
    public void update(String channelName, String videoTitle) {
        System.out.println("📧 [Email -> " + email + "] " + channelName + " just uploaded: '" + videoTitle + "'");
    }
}

class MobileAppSubscriber implements Subscriber {
    private final String username;

    public MobileAppSubscriber(String username) {
        this.username = username;
    }

    @Override
    public void update(String channelName, String videoTitle) {
        System.out.println("🔔 [Mobile Push -> @" + username + "] New video from " + channelName + ": '" + videoTitle + "'");
    }
}

class SMSNotificationSubscriber implements Subscriber {
    private final String phoneNumber;

    public SMSNotificationSubscriber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void update(String channelName, String videoTitle) {
        System.out.println("📱 [SMS -> " + phoneNumber + "] Alert: Watch " + channelName + "'s new video '" + videoTitle + "' now!");
    }
}

// =========================================================================
// 3. SUBJECT INTERFACE (Publisher Contract)
// =========================================================================

interface Channel {
    void subscribe(Subscriber subscriber);
    void unsubscribe(Subscriber subscriber);
    void notifySubscribers(String videoTitle);
}

// =========================================================================
// 4. CONCRETE SUBJECT (YouTube Channel)
// =========================================================================

class YouTubeChannel implements Channel {
    private final String channelName;
    private final List<Subscriber> subscribers = new ArrayList<>();

    public YouTubeChannel(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscribers(String videoTitle) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(channelName, videoTitle);
        }
    }

    // Business Logic: Uploading a video triggers the notification event
    public void uploadVideo(String videoTitle) {
        System.out.println("\n🎬 [" + channelName + "] Uploaded new video: '" + videoTitle + "'");
        System.out.println("📢 Broadcasting alerts to " + subscribers.size() + " subscribers...");
        notifySubscribers(videoTitle);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class ObserverPatternExample {
    public static void main(String[] args) {
        System.out.println("=== 📺 YouTube Notification System with Observer Pattern ===");

        YouTubeChannel takeUforward = new YouTubeChannel("takeUforward");

        // 1. Create Subscribers
        Subscriber raj = new MobileAppSubscriber("raj_striver");
        Subscriber rahul = new EmailSubscriber("rahul@example.com");
        Subscriber sneha = new SMSNotificationSubscriber("+91-9876543210");

        // 2. Register Subscriptions
        takeUforward.subscribe(raj);
        takeUforward.subscribe(rahul);
        takeUforward.subscribe(sneha);

        // 3. Trigger Event 1: First Video Upload
        takeUforward.uploadVideo("Observer Design Pattern in Java (Complete Guide)");

        // 4. Unsubscribe Rahul and upload Event 2
        System.out.println("\n--- 🔕 Rahul turns off notifications ---");
        takeUforward.unsubscribe(rahul);

        // 5. Trigger Event 2: Second Video Upload
        takeUforward.uploadVideo("Strategy vs State Pattern: When to Use Which?");
    }
}
