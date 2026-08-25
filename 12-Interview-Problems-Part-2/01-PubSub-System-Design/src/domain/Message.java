package domain;

public class Message {
    private final String id;
    private final String topicId;
    private final String content;
    private final long timestamp;

    public Message(String id, String topicId, String content) {
        this.id = id;
        this.topicId = topicId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String id, String topicId, String content, long timestamp) {
        this.id = id;
        this.topicId = topicId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getTopicId() { return topicId; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "Message[" + id + " | Topic: " + topicId + "] '" + content + "'";
    }
}
