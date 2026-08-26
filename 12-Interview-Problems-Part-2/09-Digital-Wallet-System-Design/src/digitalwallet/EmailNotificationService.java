package digitalwallet;

/**
 * Concrete notification channel delivering email notifications.
 */
public class EmailNotificationService implements NotificationService {

    @Override
    public void sendNotification(User user, String message) {
        if (user != null) {
            System.out.println("📧 [Email -> " + user.getEmail() + "] " + message);
        }
    }
}
