package service.notification;

import domain.NotificationMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationRouter {
    private final Map<String, NotificationChannel> channels = new ConcurrentHashMap<>();

    public NotificationRouter() {
        registerChannel(new EmailNotificationChannel());
        registerChannel(new SmsNotificationChannel());
    }

    public void registerChannel(NotificationChannel channel) {
        channels.put(channel.getChannelName().toUpperCase(), channel);
    }

    public void send(String channelName, NotificationMessage message) {
        NotificationChannel channel = channels.get(channelName.toUpperCase());
        if (channel != null) {
            channel.send(message);
        } else {
            System.out.println("🔔 [Notification] " + message);
        }
    }
}
