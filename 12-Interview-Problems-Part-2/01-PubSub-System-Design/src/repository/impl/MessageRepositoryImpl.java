package repository.impl;

import domain.Message;
import repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MessageRepositoryImpl implements MessageRepository {
    private final Map<String, Message> messages = new ConcurrentHashMap<>();

    @Override
    public Message save(Message message) {
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(String messageId) {
        return Optional.ofNullable(messages.get(messageId));
    }

    @Override
    public List<Message> findByTopic(String topicId) {
        return messages.values().stream()
                .filter(m -> m.getTopicId().equals(topicId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String messageId) {
        messages.remove(messageId);
    }
}
