package domain;

import java.time.LocalDateTime;

public class Download {
    private final int id;
    private final String downloadId;
    private final int userId;
    private final String songId;
    private final String deviceId;
    private DownloadStatus downloadStatus;
    private String localFilePath;
    private LocalDateTime downloadedAt;
    private final LocalDateTime createdAt;

    public Download(int id, String downloadId, int userId, String songId, String deviceId) {
        this.id = id;
        this.downloadId = downloadId;
        this.userId = userId;
        this.songId = songId;
        this.deviceId = deviceId;
        this.downloadStatus = DownloadStatus.PENDING;
        this.localFilePath = null;
        this.downloadedAt = null;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public int getUserId() {
        return userId;
    }

    public String getSongId() {
        return songId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public synchronized DownloadStatus getDownloadStatus() {
        return downloadStatus;
    }

    public synchronized void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public synchronized String getLocalFilePath() {
        return localFilePath;
    }

    public synchronized void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public synchronized LocalDateTime getDownloadedAt() {
        return downloadedAt;
    }

    public synchronized void setDownloadedAt(LocalDateTime downloadedAt) {
        this.downloadedAt = downloadedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public synchronized String toString() {
        return "Download[" + downloadId + " | User=" + userId + " | Song=" + songId +
                " | Status=" + downloadStatus + " | Path=" + localFilePath + "]";
    }
}
