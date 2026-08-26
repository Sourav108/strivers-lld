package digitalwallet;

/**
 * Strategy/Observer interface for delivering transactional notifications to users.
 */
public interface NotificationService {
    /**
     * Sends a transactional notification message to the user.
     *
     * @param user    the recipient user
     * @param message the notification body text
     */
    void sendNotification(User user, String message);
}
