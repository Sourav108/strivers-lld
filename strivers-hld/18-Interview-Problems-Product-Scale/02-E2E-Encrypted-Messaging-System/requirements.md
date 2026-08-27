# Staff-Level Requirements: E2E-Encrypted Messaging System

## 📋 The Staff Prompt
*"Design an end-to-end encrypted real-time messaging infrastructure (Signal / WhatsApp style) supporting 1 Billion active users, ephemeral message queues, multi-device key synchronization (Double Ratchet), and 100 Million concurrent persistent WebSocket connections with sub-100ms global delivery."*

---

## 🎯 Functional Requirements (FR)
1. **1-on-1 & Group E2EE Messaging**: Double Ratchet protocol (Signal protocol) encryption; intermediate servers cannot decrypt content.
2. **Multi-Device Support**: Message synchronization across phone, tablet, and desktop without sharing private keys.
3. **Delivery Receipts**: Sent, Delivered, and Read statuses.
4. **Offline Queueing**: Store and forward encrypted blobs when recipient reconnects.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Global Scale**: 1 Billion DAU; 50 Billion messages/day ($\approx 500,000 \text{ QPS}$).
2. **Ephemeral Privacy**: Delete messages from server disks immediately upon delivery confirmation.
3. **Connection Density**: Maintain 100M concurrent persistent WebSocket connections.
