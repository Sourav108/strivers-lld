# Requirements: Design WhatsApp / Real-Time Chat System

## 📋 Functional Requirements (FR)
1. **1-on-1 Real-Time Messaging**: Instant delivery of text messages between two users with sub-100ms latency.
2. **Message Receipts**: Track message states: Sent (`✓`), Delivered (`✓✓`), and Read (Blue `✓✓`).
3. **Group Chats**: Support group chats with up to 1,000 members.
4. **Online / Last Seen Presence**: Real-time user online presence and typing indicators.
5. **Offline Message Storage**: Queue and deliver messages when an offline recipient comes back online.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Ultra-Low Latency**: Message delivery in **`< 100ms`** globally.
2. **High Availability & Durability**: Zero message loss. 99.999% system availability.
3. **End-to-End Encryption (E2EE)**: Messages encrypted on sender device; intermediate servers cannot decrypt content.
4. **Massive Connection Density**: Maintain **100+ Million concurrent persistent WebSocket / TCP connections**.
