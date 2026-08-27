# High-Level Design: E2E-Encrypted Messaging System

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    Alice["Alice (Sender App)"] -->|Encrypted Payload over WebSocket| GW1["Chat Gateway Node 1 (Netty / Erlang)"]
    Bob["Bob (Recipient App)"] -->|WebSocket Connection| GW2["Chat Gateway Node 2"]

    subgraph KeyManagement["Public Key & Identity Tier"]
        KeySvc["Public Key Directory (PreKey Bundles / X3DH)"]
        KeyDB[("PreKey Database (DynamoDB / Cassandra)")]
        KeySvc <--> KeyDB
    end

    subgraph RoutingTier["Real-Time Session Routing"]
        SessionRegistry["Distributed Session Store (Redis Cluster)"]
        Router["Message Router Service"]
    end

    GW1 --> Router
    Router <--> SessionRegistry
    Router -->|Push to GW2 via gRPC| GW2
    GW2 -->|Deliver over socket| Bob

    subgraph OfflineForwarding["Offline Store & Forward"]
        Kafka["Kafka Offline Ingest Stream"]
        OfflineDB[("Ephemeral Store (ScyllaDB / Cassandra - TTL 30d)")]
        PushGW["Push Notification Gateway (APNS / FCM)"]
    end

    Router -->|If Bob Offline| Kafka
    Kafka --> OfflineDB
    Kafka --> PushGW
    PushGW -->|Wake device| Bob
```

---

## 🔒 2. Signal Protocol Cryptographic Primitives: X3DH & Double Ratchet
1. **X3DH (Extended Triple Diffie-Hellman)**: Establishes a shared secret key between Alice and Bob even if Bob is offline, using pre-published public Identity keys and One-Time PreKeys.
2. **Double Ratchet**: Derives a new symmetric encryption key for **every single message**. If an attacker intercepts the key for message #5, they still cannot decrypt message #6 (Forward Secrecy & Break-in Recovery)!
