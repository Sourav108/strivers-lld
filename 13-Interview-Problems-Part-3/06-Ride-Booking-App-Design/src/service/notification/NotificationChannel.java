package service.notification;

import domain.NotificationMessage;

public interface NotificationChannel {
    String getChannelName();
    void send(NotificationMessage message);
}
