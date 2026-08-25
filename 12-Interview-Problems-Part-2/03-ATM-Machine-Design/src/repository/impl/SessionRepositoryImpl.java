package repository.impl;

import domain.Session;
import repository.SessionRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRepositoryImpl implements SessionRepository {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public Session save(Session session) {
        sessions.put(session.getId(), session);
        return session;
    }

    @Override
    public Optional<Session> findById(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    @Override
    public Optional<Session> findActiveByATM(String atmId) {
        return sessions.values().stream()
                .filter(s -> s.getAtmId().equals(atmId) && s.isActive())
                .findFirst();
    }
}
