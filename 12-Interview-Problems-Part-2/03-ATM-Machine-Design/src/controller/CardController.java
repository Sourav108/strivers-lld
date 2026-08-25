package controller;

import domain.ATM;
import domain.Card;
import service.CardService;

public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    public Card registerCard(Card card) {
        return cardService.registerCard(card);
    }

    public boolean insertCard(ATM atm, String cardId) {
        cardService.validateCard(cardId);
        atm.insertCard(cardId);
        return true;
    }

    public boolean authenticateCard(ATM atm, String cardId, String pin) {
        boolean authenticated = cardService.authenticateCard(cardId, pin);
        if (authenticated) {
            atm.enterPin(pin);
        } else {
            System.out.println("❌ Incorrect PIN. Please try again.");
        }
        return authenticated;
    }

    public void ejectCard(ATM atm) {
        cardService.ejectCard(atm);
    }
}
