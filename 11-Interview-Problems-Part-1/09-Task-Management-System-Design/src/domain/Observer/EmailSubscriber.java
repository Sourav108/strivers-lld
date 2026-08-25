package domain.Observer;

import domain.ChangeType;

public class EmailSubscriber implements TaskSubscriber {
    private final String email;

    public EmailSubscriber(String email) {
        this.email = email;
    }

    @Override
    public void update(int taskId, ChangeType changeType, String oldValue, String newValue) {
        System.out.println("   📧 [Email Alert -> " + email + "] Task #" + taskId + " " + changeType + ": '" + oldValue + "' -> '" + newValue + "'");
    }

    @Override
    public String getSubscriberName() {
        return "Email:" + email;
    }
}
