package repository.impl;

import domain.Download;
import repository.DownloadRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DownloadRepositoryImpl implements DownloadRepository {
    private final Map<String, Download> downloads = new ConcurrentHashMap<>();

    @Override
    public Optional<Download> findByDownloadId(String downloadId) {
        return Optional.ofNullable(downloads.get(downloadId));
    }

    @Override
    public List<Download> findByUserIdAndDeviceId(int userId, String deviceId) {
        return downloads.values().stream()
                .filter(d -> d.getUserId() == userId && d.getDeviceId().equals(deviceId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Download> findByUserId(int userId) {
        return downloads.values().stream().filter(d -> d.getUserId() == userId).collect(Collectors.toList());
    }

    @Override
    public Download save(Download download) {
        downloads.put(download.getDownloadId(), download);
        return download;
    }

    @Override
    public void deleteByDownloadId(String downloadId) {
        downloads.remove(downloadId);
    }
}
