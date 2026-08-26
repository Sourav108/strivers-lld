package repository.impl;

import domain.PlaybackSession;
import repository.PlaybackSessionRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PlaybackSessionRepositoryImpl implements PlaybackSessionRepository {
    private final Map<String, PlaybackSession> sessions = new ConcurrentHashMap<>();

    @Override
    public Optional<PlaybackSession> findBySessionId(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    @Override
    public Optional<PlaybackSession> findByUserIdAndDeviceId(int userId, String deviceId) {
        return sessions.values().stream()
                .filter(s -> s.getUserId() == userId && s.getDeviceId().equals(deviceId))
                .findFirst();
    }

    @Override
    public PlaybackSession save(PlaybackSession session) {
        sessions.put(session.getSessionId(), session);
        return session;
    }
}
