package service.notification;

import domain.NotificationMessage;

public class SmsNotificationChannel implements NotificationChannel {

    @Override
    public String getChannelName() {
        return "SMS";
    }

    @Override
    public void send(NotificationMessage message) {
        System.out.println("📱 [SmsNotification] " + message);
    }
}
