# Trade-offs & Deep Dive: Google Docs Collaborative Editor

## ⚖️ 1. Operational Transformation (OT) vs CRDTs

| Dimension | Operational Transformation (OT) | Conflict-Free Replicated Data Types (CRDT) |
|---|---|---|
| **Architecture** | Centralized server required (Client-Server) | Decentralized / Peer-to-Peer capable |
| **Memory Overhead** | 🟢 **Minimal** (Standard character buffer) | 🔴 High (Every character requires unique fractional ID) |
| **Complexity** | Complex transformation matrix on server | High mathematical complexity in data structure |
| **Best For** | **Text documents (Google Docs, Etherpad)** | **Offline-first canvas / Figma / Local-first apps** |

---

## 💾 2. Snapshotting & Operation Compaction

- Storing every keystroke indefinitely results in an infinite sequence of operations.
- **Compaction Strategy**:
  - The document server periodically merges in-memory operations and writes a **full document snapshot** to the database every **100 operations or every 30 seconds**.
  - When a new collaborator joins the document, the server sends the **latest complete snapshot + small tail of pending delta operations**, loading the editor in $< 50\text{ms}$.
