package controller;

import domain.Session;
import service.SessionService;

public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public Session startSession(String atmId, String cardId) {
        return sessionService.startSession(atmId, cardId);
    }

    public void endSession(String sessionId) {
        sessionService.endSession(sessionId);
    }

    public Session getSession(String sessionId) {
        return sessionService.getSession(sessionId);
    }
}
