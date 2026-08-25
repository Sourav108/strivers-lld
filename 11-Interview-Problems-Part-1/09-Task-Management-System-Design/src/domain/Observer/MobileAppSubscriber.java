package domain.Observer;

import domain.ChangeType;

public class MobileAppSubscriber implements TaskSubscriber {
    private final String deviceToken;

    public MobileAppSubscriber(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @Override
    public void update(int taskId, ChangeType changeType, String oldValue, String newValue) {
        System.out.println("   📱 [Push Notification -> " + deviceToken + "] Task #" + taskId + " " + changeType + ": " + newValue);
    }

    @Override
    public String getSubscriberName() {
        return "Device:" + deviceToken;
    }
}
