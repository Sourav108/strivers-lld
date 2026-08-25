package repository;

import domain.DeliveryStatus;
import domain.MessageDelivery;
import java.util.List;
import java.util.Optional;

public interface MessageDeliveryRepository {
    MessageDelivery save(MessageDelivery delivery);
    Optional<MessageDelivery> findById(String deliveryId);
    List<MessageDelivery> findPendingBySubscriber(String subscriberId);
    List<MessageDelivery> findByMessage(String messageId);
    void updateDeliveryStatus(String deliveryId, DeliveryStatus status);
    void deleteById(String deliveryId);
}
