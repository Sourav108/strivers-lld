package domain;

import java.time.LocalDateTime;

public class ListeningHistory {
    private final int id;
    private final int userId;
    private final String songId;
    private final LocalDateTime playedAt;
    private final long playDuration; // seconds
    private final boolean completed;

    public ListeningHistory(int id, int userId, String songId, long playDuration, boolean completed) {
        this.id = id;
        this.userId = userId;
        this.songId = songId;
        this.playedAt = LocalDateTime.now();
        this.playDuration = playDuration;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getSongId() {
        return songId;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public long getPlayDuration() {
        return playDuration;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public String toString() {
        return "ListeningHistory[User=" + userId + ", Song=" + songId + ", Duration=" + playDuration + "s, Completed=" + completed + "]";
    }
}
