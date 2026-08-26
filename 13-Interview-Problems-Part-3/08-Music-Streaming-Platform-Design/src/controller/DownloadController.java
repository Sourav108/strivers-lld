package controller;

import domain.Download;
import service.DownloadService;

import java.util.List;

public class DownloadController {
    private final DownloadService downloadService;

    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    public Download download(int userId, String songId, String deviceId) {
        return downloadService.download(userId, songId, deviceId);
    }

    public List<Download> getDownloads(int userId, String deviceId) {
        return downloadService.getDownloads(userId, deviceId);
    }

    public void deleteDownload(String downloadId, int userId) {
        downloadService.deleteDownload(downloadId, userId);
    }
}
