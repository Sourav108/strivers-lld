package repository;

import domain.PlaybackSession;

import java.util.Optional;

public interface PlaybackSessionRepository {
    Optional<PlaybackSession> findBySessionId(String sessionId);
    Optional<PlaybackSession> findByUserIdAndDeviceId(int userId, String deviceId);
    PlaybackSession save(PlaybackSession session);
}
