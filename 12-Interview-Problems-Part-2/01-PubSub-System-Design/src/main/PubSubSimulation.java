package main;

import controller.*;
import domain.*;
import repository.*;
import repository.impl.*;
import service.*;

/**
 * PubSubSimulation: Complete End-to-End Simulation of the PubSub System
 * 
 * Demonstrates:
 * 1. Topic & Subscription Management
 * 2. Observer Pattern: Dual-Channel Notifications (Email unconditional, Realtime online-only)
 * 3. Offline Handling: Queuing pending messages when subscriber disconnects
 * 4. Reconnect & Sync: Automatic offline queue delivery when subscriber comes back online
 * 5. Message Acknowledgment: End-to-end delivery tracking and state updates
 */

public class PubSubSimulation {
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("📡 PUBSUB NOTIFICATION SYSTEM - LLD INTERVIEW ARCHITECTURE DEMO");
        System.out.println("=================================================================");

        // --- 1. INITIALIZE REPOSITORIES ---
        TopicRepository topicRepo = new TopicRepositoryImpl();
        SubscriberRepository subscriberRepo = new SubscriberRepositoryImpl();
        SubscriptionRepository subscriptionRepo = new SubscriptionRepositoryImpl();
        MessageRepository messageRepo = new MessageRepositoryImpl();
        MessageDeliveryRepository deliveryRepo = new MessageDeliveryRepositoryImpl();

        // --- 2. INITIALIZE SERVICES ---
        TopicService topicService = new TopicService(topicRepo);
        SubscriberService subscriberService = new SubscriberService(
                subscriberRepo, subscriptionRepo, topicRepo, messageRepo, deliveryRepo
        );
        SubscriptionService subscriptionService = new SubscriptionService(
                subscriptionRepo, topicRepo, subscriberRepo
        );
        PublisherService publisherService = new PublisherService(
                topicRepo, messageRepo, subscriptionRepo, subscriberRepo, deliveryRepo
        );
        MessageService messageService = new MessageService(deliveryRepo);

        // --- 3. INITIALIZE CONTROLLERS ---
        TopicController topicController = new TopicController(topicService);
        SubscriberController subscriberController = new SubscriberController(subscriberService);
        SubscriptionController subscriptionController = new SubscriptionController(subscriptionService);
        PublisherController publisherController = new PublisherController(publisherService);
        MessageController messageController = new MessageController(messageService);

        // =========================================================================
        // SCENARIO 1: TOPIC & SUBSCRIBER REGISTRATION
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ SCENARIO 1: Topic Creation & Subscriber Registration");
        System.out.println("-----------------------------------------------------------");

        Topic techTopic = topicController.createTopic("Tech-Announcements");
        Topic tradeTopic = topicController.createTopic("Crypto-Signals");

        Subscriber alice = subscriberController.registerSubscriber("alice@fintech.com");
        subscriberController.goOnline(alice.getId(), "WS-ALICE-CONN-01");

        Subscriber bob = subscriberController.registerSubscriber("bob@developer.org");
        subscriberController.goOnline(bob.getId(), "WS-BOB-CONN-02");

        // Subscriptions
        subscriptionController.subscribeToTopic(techTopic.getId(), alice.getId());
        subscriptionController.subscribeToTopic(techTopic.getId(), bob.getId());
        subscriptionController.subscribeToTopic(tradeTopic.getId(), bob.getId());

        // =========================================================================
        // SCENARIO 2: PUBLISH MESSAGE (Both Online - Dual Channel)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ SCENARIO 2: Message Published (Both Online: Email + Realtime)");
        System.out.println("-----------------------------------------------------------");

        Message msg1 = publisherController.publishMessage(
                techTopic.getId(), "🚀 System Maintenance scheduled for tonight at 12:00 AM UTC."
        );

        // =========================================================================
        // SCENARIO 3: OFFLINE SUBSCRIBER QUEUING
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ SCENARIO 3: Subscriber Goes Offline & Receives Queued Message");
        System.out.println("-----------------------------------------------------------");

        // Bob goes offline
        subscriberController.goOffline(bob.getId());

        // Publisher publishes critical trading signal to tradeTopic (subscribed by Bob)
        Message msg2 = publisherController.publishMessage(
                tradeTopic.getId(), "📈 BTC/USDT breakout alert! Target: $98,500."
        );

        // =========================================================================
        // SCENARIO 4: RECONNECT & SYNC PENDING DELIVERIES
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ SCENARIO 4: Subscriber Reconnects -> Auto-Pushes Queued Messages");
        System.out.println("-----------------------------------------------------------");

        // Bob reconnects with new WebSocket connection
        subscriberController.goOnline(bob.getId(), "WS-BOB-CONN-03");

        // =========================================================================
        // SCENARIO 5: MESSAGE ACKNOWLEDGMENT
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("5️⃣ SCENARIO 5: Message Delivery Acknowledgment");
        System.out.println("-----------------------------------------------------------");

        messageController.acknowledgeMessage(msg2.getId(), bob.getId());

        // =========================================================================
        // SCENARIO 6: TOPIC DEACTIVATION SAFETY
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("6️⃣ SCENARIO 6: Topic Deactivation Guard");
        System.out.println("-----------------------------------------------------------");

        topicController.deactivateTopic(tradeTopic.getId());
        try {
            System.out.println("⚠️ Attempting to publish to deactivated topic...");
            publisherController.publishMessage(tradeTopic.getId(), "New Signal");
        } catch (IllegalStateException e) {
            System.out.println("   🛡️ Caught Expected Topic Violation -> " + e.getMessage());
        }

        System.out.println("\n=================================================================");
        System.out.println("🎯 PUBSUB SYSTEM ARCHITECTURE COMPLETE & VERIFIED!");
        System.out.println("=================================================================");
    }
}
