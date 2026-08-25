package repository.impl;

import domain.DeliveryStatus;
import domain.MessageDelivery;
import repository.MessageDeliveryRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MessageDeliveryRepositoryImpl implements MessageDeliveryRepository {
    private final Map<String, MessageDelivery> deliveries = new ConcurrentHashMap<>();

    @Override
    public MessageDelivery save(MessageDelivery delivery) {
        deliveries.put(delivery.getId(), delivery);
        return delivery;
    }

    @Override
    public Optional<MessageDelivery> findById(String deliveryId) {
        return Optional.ofNullable(deliveries.get(deliveryId));
    }

    @Override
    public List<MessageDelivery> findPendingBySubscriber(String subscriberId) {
        return deliveries.values().stream()
                .filter(d -> d.getSubscriberId().equals(subscriberId) && d.getStatus() == DeliveryStatus.PENDING)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDelivery> findByMessage(String messageId) {
        return deliveries.values().stream()
                .filter(d -> d.getMessageId().equals(messageId))
                .collect(Collectors.toList());
    }

    @Override
    public void updateDeliveryStatus(String deliveryId, DeliveryStatus status) {
        MessageDelivery delivery = deliveries.get(deliveryId);
        if (delivery != null) {
            delivery.setStatus(status);
        }
    }

    @Override
    public void deleteById(String deliveryId) {
        deliveries.remove(deliveryId);
    }
}
