# High-Level Design: WhatsApp / Real-Time Chat System

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    UserA["User A (Sender)"] -->|WebSocket Connection| GatewayA["Chat Gateway Node 1 (Netty / Erlang)"]
    UserB["User B (Online Recipient)"] -->|WebSocket Connection| GatewayB["Chat Gateway Node 2"]

    subgraph ChatRoutingCore["Real-Time Routing & Session Tier"]
        SessionRegistry["Distributed Session Registry (Redis Cluster)<br/>Maps: User ID -> Gateway Node IP"]
        MessageRouter["Message Routing Service"]
    end

    GatewayA --> MessageRouter
    MessageRouter <--> SessionRegistry
    MessageRouter -->|Push via internal RPC| GatewayB
    GatewayB -->|Deliver over socket| UserB

    subgraph OfflineStorage["Offline & Group Tier"]
        Kafka["Kafka Message Stream"]
        OfflineQueue["Offline Message Store (Cassandra / ScyllaDB)"]
        PushSvc["Push Notification Gateway (APNS / FCM)"]
        GroupSvc["Group Chat Fanout Engine"]
    end

    MessageRouter -->|If User B Offline| Kafka
    Kafka --> OfflineQueue
    Kafka --> PushSvc
    PushSvc -->|Wake up device| UserB
```

---

## ⚡ 2. End-to-End Message Delivery Flow

```mermaid
sequenceDiagram
    autonumber
    actor Alice as Alice (Sender)
    participant GW1 as Gateway Server 1
    participant Router as Message Routing Service
    participant Redis as Session Registry (Redis)
    participant GW2 as Gateway Server 2
    actor Bob as Bob (Recipient)

    Alice->>GW1: Send Message { to: "Bob", payload: "Hello", msg_id: "m_10" }
    GW1-->>Alice: Ack (Received by Server `✓`)
    GW1->>Router: Route Message to Bob
    Router->>Redis: GET session:Bob
    Redis-->>Router: Returns "GW2_IP:10.0.5.21"
    Router->>GW2: Forward Message via gRPC
    GW2->>Bob: Push Message over Bob's WebSocket
    Bob-->>GW2: Ack (Delivered to Device `✓✓`)
    GW2->>Router: Delivery Receipt Notification
    Router->>GW1: Forward Delivery Ack
    GW1->>Alice: Push Delivery Receipt `✓✓` to Alice
```

---

## 🗄️ 3. Data Model (Cassandra Wide-Column Store)

```sql
CREATE TABLE user_messages (
    user_id BIGINT,
    message_id BIGINT, -- Snowflake ID
    sender_id BIGINT,
    conversation_id VARCHAR(64),
    encrypted_content BLOB,
    status VARCHAR(16), -- SENT, DELIVERED, READ
    created_at TIMESTAMP,
    PRIMARY KEY ((user_id), message_id)
) WITH CLUSTERING ORDER BY (message_id DESC);
```
