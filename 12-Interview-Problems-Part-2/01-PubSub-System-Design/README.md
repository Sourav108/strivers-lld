# PubSub Notification System - Low-Level Design

## 1. Problem Statement

Design a robust, scalable, and resilient **Publish-Subscribe (PubSub) Notification System** supporting multiple topics, dynamic subscription management, dual-channel message dispatch (**Email** for asynchronous persistence and **Realtime WebSockets** for live delivery), offline subscriber message queuing, automatic reconnection replay, and end-to-end delivery acknowledgment.

---

## 2. Requirements

### Functional Requirements
- **Topic Management:** Create, list, query, and deactivate topics dynamically.
- **Publisher Operations:** Publishers can publish messages to any active topic.
- **Subscription Management:** Subscribers can subscribe and unsubscribe from specific topics.
- **Dual-Channel Message Delivery (Observer Pattern):**
  - **Email Channel:** Dispatched unconditionally to all registered topic subscribers.
  - **Realtime Channel:** Delivered immediately if subscriber is online (`isOnline == true`); queued as `PENDING` if subscriber is offline.
- **Offline Queuing & Reconnection Sync:** When an offline subscriber reconnects (`goOnline()`), all accumulated `PENDING` messages are immediately pushed to their active realtime connection.
- **Message Acknowledgment:** Subscribers can acknowledge message delivery, updating delivery records to `ACKNOWLEDGED`.

### Important Non-Functional Requirements
- **Thread Safety:** Safe concurrent access across topic subscriptions, message publishing, and subscriber status changes (`CopyOnWriteArrayList`, `ConcurrentHashMap`).
- **Decoupled Architecture:** Clean separation of concerns across Controller, Service, Repository, and Domain layers.
- **Extensibility:** Easily add new notification channels (e.g. SMS, Webhooks, Push Notifications) via the Observer interface.

---

## 3. Package Structure

```
src/
├── controller/
│   ├── MessageController.java
│   ├── PublisherController.java
│   ├── SubscriberController.java
│   ├── SubscriptionController.java
│   └── TopicController.java
├── domain/
│   ├── observer/
│   │   ├── EmailSubscriber.java
│   │   ├── MessageSubject.java
│   │   ├── RealtimeSubscriber.java
│   │   └── SubscriberObserver.java   (Observer Interface)
│   ├── DeliveryChannel.java          (Enum: EMAIL, REALTIME)
│   ├── DeliveryStatus.java           (Enum: PENDING, DELIVERED, ACKNOWLEDGED)
│   ├── Message.java
│   ├── MessageDelivery.java
│   ├── Priority.java                 (Enum: LOW, MEDIUM, HIGH)
│   ├── Subscriber.java
│   ├── Subscription.java
│   └── Topic.java
├── repository/
│   ├── impl/
│   │   ├── MessageDeliveryRepositoryImpl.java
│   │   ├── MessageRepositoryImpl.java
│   │   ├── SubscriberRepositoryImpl.java
│   │   ├── SubscriptionRepositoryImpl.java
│   │   └── TopicRepositoryImpl.java
│   ├── MessageDeliveryRepository.java (Interface)
│   ├── MessageRepository.java         (Interface)
│   ├── SubscriberRepository.java      (Interface)
│   ├── SubscriptionRepository.java    (Interface)
│   └── TopicRepository.java           (Interface)
├── service/
│   ├── MessageService.java
│   ├── PublisherService.java
│   ├── SubscriberService.java
│   ├── SubscriptionService.java
│   └── TopicService.java
└── main/
    └── PubSubSimulation.java          (Driver Simulation)
```

---

## 4. Class Responsibilities

| Package | Class / Interface | Responsibility (1 Line) |
|---|---|---|
| `domain` | **`Topic`** | Aggregate root managing topic metadata and internal `MessageSubject`. |
| `domain` | **`Subscriber`** | Represents a subscriber with email, online status, and active connection ID. |
| `domain` | **`Subscription`** | Persistent mapping between a Subscriber and a Topic. |
| `domain` | **`Message`** | Immutable data carrier containing topic ID, content, and timestamp. |
| `domain` | **`MessageDelivery`** | Tracks per-subscriber channel delivery status (`PENDING`, `DELIVERED`, `ACKNOWLEDGED`). |
| `domain.observer` | **`SubscriberObserver`** | Observer interface defining the `update(Message)` contract. |
| `domain.observer` | **`EmailSubscriber`** | Concrete observer dispatching asynchronous email notifications. |
| `domain.observer` | **`RealtimeSubscriber`** | Concrete observer streaming live messages over active WebSocket connections. |
| `domain.observer` | **`MessageSubject`** | Manages thread-safe subscriber observer lists per topic and coordinates broadcasts. |
| `repository` | **`TopicRepository`**, **`MessageRepository`**, etc. | Storage contracts for topics, subscribers, subscriptions, messages, and delivery logs. |
| `repository.impl` | **`*RepositoryImpl`** | Thread-safe in-memory storage implementations using `ConcurrentHashMap`. |
| `service` | **`PublisherService`** | Validates topics, persists messages, tracks channel deliveries, and triggers broadcasts. |
| `service` | **`SubscriberService`** | Manages subscriber registration, online/offline lifecycle, and offline queue sync. |
| `service` | **`SubscriptionService`** | Links subscribers to topics and attaches channel observers. |
| `service` | **`MessageService`** | Handles delivery status acknowledgment. |
| `service` | **`TopicService`** | Handles topic lifecycle (creation, lookup, deactivation). |
| `controller` | **`*Controller`** | REST-style API coordinators delegating requests to corresponding services. |
| `main` | **`PubSubSimulation`** | Executable simulation testing all dual-channel, offline queue, and ack scenarios. |

---

## 5. Design Patterns & SOLID Principles

- **Observer Pattern:**
  - `MessageSubject` (Subject) notifies `SubscriberObserver` instances (`EmailSubscriber`, `RealtimeSubscriber`) on message publishing.
- **Repository Pattern:**
  - Decouples domain entities and services from specific persistence layers.
- **Single Responsibility Principle (SRP):**
  - Distinct services for publishing (`PublisherService`), subscriber status (`SubscriberService`), subscriptions (`SubscriptionService`), and topic lifecycle (`TopicService`).
- **Open/Closed Principle (OCP):**
  - Adding a new delivery channel (e.g. `WebhookSubscriber`) requires creating an implementation of `SubscriberObserver` without touching existing publisher or topic code.
- **Dependency Inversion Principle (DIP):**
  - High-level services depend on repository interfaces (`TopicRepository`, `MessageRepository`), not concrete HashMap storage.

---

## 6. Main Flows

### Flow 1: Message Publishing (Dual Channel)
```
PublisherController.publishMessage(topicId, "Alert Message")
  -> PublisherService verifies Topic.isActive() == true
  -> Persists Message in MessageRepository
  -> For each subscriber:
     -> If Online: Creates DELIVERED records for Email & Realtime
     -> If Offline: Creates DELIVERED for Email, PENDING for Realtime
  -> Calls topic.getMessageSubject().notify(message)
     -> EmailSubscriber dispatches email
     -> RealtimeSubscriber dispatches WebSocket message
```

### Flow 2: Offline Subscriber Reconnect & Pending Message Push
```
SubscriberController.goOnline(subscriberId, "WS-CONN-NEW")
  -> SubscriberService updates Subscriber(isOnline = true)
  -> Attaches RealtimeSubscriber to all subscribed topics
  -> Calls pushPendingDeliveries(subscriberId)
     -> Finds all MessageDelivery with status == PENDING
     -> Dispatches queued messages via realtime connection
     -> Updates status to DELIVERED
```

---

## 7. Edge Cases Handled

1. **Subscriber Offline During Broadcast:** Email is delivered normally; Realtime message is saved with `PENDING` status in `MessageDeliveryRepository`.
2. **Reconnection after Long Disconnect:** Upon `goOnline()`, `SubscriberService` queries all `PENDING` deliveries and drains the offline queue.
3. **Publishing to Inactive/Deactivated Topic:** Throws `IllegalStateException` preventing phantom message distribution.
4. **Duplicate WebSocket Connection:** `MessageSubject` cleans up old connections for the same subscriber when a new connection is registered.

---

## 8. How to Run

Compile and execute from the `01-PubSub-System-Design` directory:

```bash
# Compile all packaged Java source files
javac -d bin $(find src -name "*.java")

# Run the complete demonstration driver
java -cp bin main.PubSubSimulation
```

---

## 9. Interview Thinking

### How I Would Explain This in an Interview
1. **Step 1 (Clarify Requirements):** Focus on topic publishing, multi-subscriber broadcasts, dual-channel dispatch (Email vs Realtime), and offline reliability.
2. **Step 2 (Identify Entities):** `Topic`, `Subscriber`, `Subscription`, `Message`, `MessageDelivery`, `DeliveryChannel`, `DeliveryStatus`.
3. **Step 3 (Observer Architecture):** Use the Observer pattern with separated subscriber observer lists (`emailSubscribers` vs `realtimeSubscribers`) to isolate delivery concerns.
4. **Step 4 (Offline Queueing & Sync):** Detail how `PENDING` delivery states allow seamless offline catchup upon `goOnline()`.

### Likely Interviewer Follow-up Questions
1. *How would you scale this system across multiple server nodes?*
   - **Answer:** Replace in-memory subject dispatch with a distributed message broker (e.g. Apache Kafka / Redis PubSub) with consumer group partitioning and Redis for WebSocket session routing.
2. *How do you guarantee Exactly-Once delivery?*
   - **Answer:** Use idempotent message IDs (`msgId`), deduplication windows on the client/subscriber side, and two-phase delivery acknowledgments.

---

## 🎯 Quick Summary

- **Problem:** Design a multi-topic PubSub notification system with dual-channel delivery, offline queueing, and message acknowledgment.
- **Core Classes:** `Topic`, `Subscriber`, `Message`, `MessageDelivery`, `MessageSubject`, `SubscriberObserver` (`EmailSubscriber`, `RealtimeSubscriber`).
- **Main Flow:** Publish Message $\rightarrow$ Record Deliveries $\rightarrow$ Broadcast via Email & Realtime $\rightarrow$ Queue for Offline $\rightarrow$ Sync on Reconnect $\rightarrow$ Acknowledge.
- **Important Design:** Observer Pattern with dual channels (Email unconditional, Realtime online-only); Repository pattern for delivery audit.
- **Edge Cases:** Offline subscriber queuing, reconnect replay, duplicate connection cleanup, and deactivated topic rejection.
- **LLD Takeaway:** Separate delivery channels within the Observer pattern to handle varying delivery guarantees and online/offline states cleanly.
- **Memorable Rule:** *"Email always delivers, Realtime checks presence, and Reconnection drains the pending queue."*
