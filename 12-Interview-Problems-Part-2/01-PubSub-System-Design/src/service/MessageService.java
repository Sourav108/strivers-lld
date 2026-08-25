package service;

import domain.DeliveryStatus;
import domain.MessageDelivery;
import repository.MessageDeliveryRepository;

import java.util.List;

public class MessageService {
    private final MessageDeliveryRepository deliveryRepository;

    public MessageService(MessageDeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public synchronized void acknowledgeMessage(String messageId, String subscriberId) {
        List<MessageDelivery> deliveries = deliveryRepository.findByMessage(messageId);
        boolean found = false;
        for (MessageDelivery delivery : deliveries) {
            if (delivery.getSubscriberId().equals(subscriberId)) {
                delivery.acknowledge();
                deliveryRepository.save(delivery);
                found = true;
                System.out.println("✅ [Message Acknowledged] Subscriber #" + subscriberId + " acknowledged Message #" + messageId);
            }
        }
        if (!found) {
            System.out.println("⚠️ No pending delivery found to acknowledge for Message #" + messageId + " by Subscriber #" + subscriberId);
        }
    }
}
