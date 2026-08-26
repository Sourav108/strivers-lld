package service.notification;

import domain.NotificationMessage;

public class EmailNotificationChannel implements NotificationChannel {

    @Override
    public String getChannelName() {
        return "EMAIL";
    }

    @Override
    public void send(NotificationMessage message) {
        System.out.println("📧 [EmailNotification] " + message);
    }
}
