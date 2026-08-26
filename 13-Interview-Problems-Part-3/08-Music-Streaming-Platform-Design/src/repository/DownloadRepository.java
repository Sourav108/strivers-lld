package repository;

import domain.Download;

import java.util.List;
import java.util.Optional;

public interface DownloadRepository {
    Optional<Download> findByDownloadId(String downloadId);
    List<Download> findByUserIdAndDeviceId(int userId, String deviceId);
    List<Download> findByUserId(int userId);
    Download save(Download download);
    void deleteByDownloadId(String downloadId);
}
