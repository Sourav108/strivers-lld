package service;

import domain.ATM;
import domain.Card;
import domain.exception.CardBlockedException;
import repository.CardRepository;

public class CardService {
    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card registerCard(Card card) {
        cardRepository.save(card);
        return card;
    }

    public Card getCard(String cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card #" + cardId + " not found."));
    }

    public boolean validateCard(String cardId) {
        Card card = getCard(cardId);
        if (card.isBlocked()) {
            throw new CardBlockedException("🚫 Card #" + cardId + " is BLOCKED.");
        }
        return true;
    }

    public boolean authenticateCard(String cardId, String pin) {
        Card card = getCard(cardId);
        boolean success = card.authenticate(pin);
        cardRepository.save(card);
        return success;
    }

    public void ejectCard(ATM atm) {
        atm.ejectCard();
    }
}
