package repository;

import domain.Message;
import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(String messageId);
    List<Message> findByTopic(String topicId);
    void deleteById(String messageId);
}
