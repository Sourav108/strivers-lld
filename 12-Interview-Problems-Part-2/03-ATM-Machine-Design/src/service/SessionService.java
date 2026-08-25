package service;

import domain.ATM;
import domain.Card;
import domain.Session;
import repository.ATMRepository;
import repository.CardRepository;
import repository.SessionRepository;

import java.util.UUID;

public class SessionService {
    private final SessionRepository sessionRepository;
    private final ATMRepository atmRepository;
    private final CardRepository cardRepository;

    public SessionService(SessionRepository sessionRepository, ATMRepository atmRepository, CardRepository cardRepository) {
        this.sessionRepository = sessionRepository;
        this.atmRepository = atmRepository;
        this.cardRepository = cardRepository;
    }

    public synchronized Session startSession(String atmId, String cardId) {
        ATM atm = atmRepository.findById(atmId)
                .orElseThrow(() -> new IllegalArgumentException("ATM #" + atmId + " not found."));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card #" + cardId + " not found."));

        String sessionId = "SESS-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Session session = new Session(sessionId, atmId, cardId, card.getAccountId());
        sessionRepository.save(session);
        atm.setCurrentSession(session);
        return session;
    }

    public synchronized void endSession(String sessionId) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.endSession();
            sessionRepository.save(session);
            atmRepository.findById(session.getAtmId()).ifPresent(atm -> {
                atm.setCurrentSession(null);
                atm.ejectCard();
            });
        });
    }

    public Session getSession(String sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session #" + sessionId + " not found."));
    }
}
