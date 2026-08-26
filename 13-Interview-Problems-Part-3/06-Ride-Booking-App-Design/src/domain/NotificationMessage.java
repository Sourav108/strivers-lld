package domain;

public class NotificationMessage {
    private final String recipient;
    private final String title;
    private final String body;

    public NotificationMessage(String recipient, String title, String body) {
        this.recipient = recipient;
        this.title = title;
        this.body = body;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "[" + title + " -> " + recipient + "] " + body;
    }
}
