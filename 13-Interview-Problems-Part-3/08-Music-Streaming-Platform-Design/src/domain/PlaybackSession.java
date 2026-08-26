package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaybackSession {
    private final int id;
    private final String sessionId;
    private final int userId;
    private String currentSongId;
    private long currentPosition; // seconds
    private final PlaybackSource playbackSource;
    private final String sourceId;
    private List<String> queue;
    private int currentIndex;
    private boolean shuffleMode;
    private RepeatMode repeatMode;
    private PlaybackStatus status;
    private final String deviceId;
    private final LocalDateTime startedAt;
    private LocalDateTime lastUpdatedAt;

    public PlaybackSession(int id, String sessionId, int userId, String initialSongId,
                           PlaybackSource playbackSource, String sourceId, List<String> queue,
                           String deviceId) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.currentSongId = initialSongId;
        this.currentPosition = 0;
        this.playbackSource = playbackSource;
        this.sourceId = sourceId;
        this.queue = new ArrayList<>(queue != null ? queue : Collections.emptyList());
        this.currentIndex = Math.max(0, this.queue.indexOf(initialSongId));
        this.shuffleMode = false;
        this.repeatMode = RepeatMode.OFF;
        this.status = PlaybackStatus.PLAYING;
        this.deviceId = deviceId;
        this.startedAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public synchronized String getCurrentSongId() {
        return currentSongId;
    }

    public synchronized long getCurrentPosition() {
        return currentPosition;
    }

    public synchronized void updatePosition(long position) {
        this.currentPosition = position;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public PlaybackSource getPlaybackSource() {
        return playbackSource;
    }

    public String getSourceId() {
        return sourceId;
    }

    public synchronized List<String> getQueue() {
        return Collections.unmodifiableList(queue);
    }

    public synchronized boolean isShuffleMode() {
        return shuffleMode;
    }

    public synchronized void setShuffleMode(boolean shuffleMode) {
        this.shuffleMode = shuffleMode;
        if (shuffleMode && queue.size() > 1) {
            String current = currentSongId;
            Collections.shuffle(queue);
            this.currentIndex = queue.indexOf(current);
        }
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public synchronized RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public synchronized void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public synchronized PlaybackStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(PlaybackStatus status) {
        this.status = status;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public synchronized String skipNext() {
        if (queue.isEmpty()) return null;

        if (repeatMode == RepeatMode.ONE) {
            this.currentPosition = 0;
            this.lastUpdatedAt = LocalDateTime.now();
            return currentSongId;
        }

        if (currentIndex + 1 < queue.size()) {
            currentIndex++;
            currentSongId = queue.get(currentIndex);
            currentPosition = 0;
        } else if (repeatMode == RepeatMode.ALL) {
            currentIndex = 0;
            currentSongId = queue.get(0);
            currentPosition = 0;
        } else {
            status = PlaybackStatus.STOPPED;
        }
        this.lastUpdatedAt = LocalDateTime.now();
        return currentSongId;
    }

    public synchronized String skipPrevious() {
        if (queue.isEmpty()) return null;

        if (currentIndex > 0) {
            currentIndex--;
            currentSongId = queue.get(currentIndex);
            currentPosition = 0;
        } else {
            currentPosition = 0;
        }
        this.lastUpdatedAt = LocalDateTime.now();
        return currentSongId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public synchronized LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    @Override
    public synchronized String toString() {
        return "PlaybackSession[" + sessionId + " | Status=" + status +
                " | Song=" + currentSongId + " (" + currentPosition + "s)" +
                " | Queue=" + queue.size() + " songs | Repeat=" + repeatMode +
                " | Shuffle=" + shuffleMode + "]";
    }
}
