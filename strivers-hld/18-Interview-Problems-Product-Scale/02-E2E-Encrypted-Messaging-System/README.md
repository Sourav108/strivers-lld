# High-Level Design: E2E-Encrypted Messaging System

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    Alice["Alice (Sender)"] -->|WebSocket| GW1["Chat Gateway 1"]
    Bob["Bob (Recipient)"] -->|WebSocket| GW2["Chat Gateway 2"]

    subgraph KeyTier["Public Key Directory"]
        KeySvc["PreKey Service"]
        KeyDB[("PreKey DB (DynamoDB)")]
        KeySvc <--> KeyDB
    end

    subgraph RoutingTier["Session Routing"]
        SessionRegistry["Session Registry (Redis)"]
        Router["Message Router"]
    end

    GW1 --> Router
    Router <--> SessionRegistry
    Router -->|gRPC| GW2
    GW2 --> Bob

    subgraph OfflineTier["Offline Queue"]
        Kafka["Kafka Stream"]
        OfflineDB[("Ephemeral DB (TTL 30d)")]
        PushGW["Push Gateway (APNS/FCM)"]
    end

    Router -->|If Bob Offline| Kafka
    Kafka --> OfflineDB
    Kafka --> PushGW
    PushGW --> Bob
```

---

## 🔒 2. Signal Protocol Cryptographic Primitives: X3DH & Double Ratchet
1. **X3DH (Extended Triple Diffie-Hellman)**: Establishes a shared secret key between Alice and Bob even if Bob is offline, using pre-published public Identity keys and One-Time PreKeys.
2. **Double Ratchet**: Derives a new symmetric encryption key for **every single message**. If an attacker intercepts the key for message #5, they still cannot decrypt message #6 (Forward Secrecy & Break-in Recovery)!
