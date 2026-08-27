# Staff Signals: E2E-Encrypted Messaging System

## 🎯 Staff-Level Grading Criteria:
- **E2EE Protocol Knowledge**: Demonstrates understanding of X3DH pre-keys, Double Ratchet forward secrecy, and identity verification.
- **Connection Density Optimization**: Details Linux epoll / Netty socket memory footprint ($\approx 10\text{KB}$ per socket) and zero-copy TCP delivery.
- **Group Chat Fan-out Trade-offs**: Balances client-side encryption (Sender-Keys protocol) with server-side network fanout.
