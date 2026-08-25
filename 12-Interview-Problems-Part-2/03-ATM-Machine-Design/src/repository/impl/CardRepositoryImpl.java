package repository.impl;

import domain.Card;
import repository.CardRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CardRepositoryImpl implements CardRepository {
    private final Map<String, Card> cards = new ConcurrentHashMap<>();

    @Override
    public Card save(Card card) {
        cards.put(card.getId(), card);
        return card;
    }

    @Override
    public Optional<Card> findById(String cardId) {
        return Optional.ofNullable(cards.get(cardId));
    }
}
