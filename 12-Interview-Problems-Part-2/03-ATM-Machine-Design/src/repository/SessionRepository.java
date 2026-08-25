package repository;

import domain.Session;
import java.util.Optional;

public interface SessionRepository {
    Session save(Session session);
    Optional<Session> findById(String sessionId);
    Optional<Session> findActiveByATM(String atmId);
}
