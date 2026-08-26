package service;

import domain.Download;
import domain.DownloadStatus;
import domain.User;
import repository.DownloadRepository;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadService {
    private final DownloadRepository downloadRepository;
    private final UserRepository userRepository;
    private final StreamingService streamingService;

    private static final int MAX_DEVICES_PER_USER = 5;
    private static final int MAX_DOWNLOADS_PER_USER = 10000;
    private final AtomicInteger downloadIdCounter = new AtomicInteger(1);

    public DownloadService(DownloadRepository downloadRepository, UserRepository userRepository, StreamingService streamingService) {
        this.downloadRepository = downloadRepository;
        this.userRepository = userRepository;
        this.streamingService = streamingService;
    }

    public Download download(int userId, String songId, String deviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User " + userId + " not found."));

        if (!user.isPremium()) {
            throw new IllegalStateException("Offline downloads are exclusive to PREMIUM subscribers.");
        }

        if (!validateDeviceLimit(userId, deviceId)) {
            throw new IllegalStateException("Maximum device limit (" + MAX_DEVICES_PER_USER + ") exceeded for user.");
        }

        if (!validateDownloadLimit(userId)) {
            throw new IllegalStateException("Maximum download limit (" + MAX_DOWNLOADS_PER_USER + ") exceeded.");
        }

        int id = downloadIdCounter.getAndIncrement();
        String downloadId = "DL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Download download = new Download(id, downloadId, userId, songId, deviceId);
        downloadRepository.save(download);

        // Simulate asynchronous file caching
        streamingService.downloadFullSong(songId, deviceId);
        download.setDownloadStatus(DownloadStatus.COMPLETED);
        download.setLocalFilePath("/data/music/" + songId + ".mp3");
        download.setDownloadedAt(LocalDateTime.now());
        downloadRepository.save(download);

        System.out.println("✅ " + download + " ready for offline playback.");
        return download;
    }

    public List<Download> getDownloads(int userId, String deviceId) {
        return downloadRepository.findByUserIdAndDeviceId(userId, deviceId);
    }

    public void deleteDownload(String downloadId, int userId) {
        downloadRepository.deleteByDownloadId(downloadId);
        System.out.println("🗑️ Download " + downloadId + " deleted for user " + userId);
    }

    public boolean validateDeviceLimit(int userId, String deviceId) {
        List<Download> existing = downloadRepository.findByUserId(userId);
        long distinctDevices = existing.stream().map(Download::getDeviceId).distinct().count();
        return distinctDevices < MAX_DEVICES_PER_USER || existing.stream().anyMatch(d -> d.getDeviceId().equals(deviceId));
    }

    public boolean validateDownloadLimit(int userId) {
        return downloadRepository.findByUserId(userId).size() < MAX_DOWNLOADS_PER_USER;
    }
}
