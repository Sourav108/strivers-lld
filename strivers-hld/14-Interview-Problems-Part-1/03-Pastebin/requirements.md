# Requirements: Design Pastebin / Gist

## 📋 Functional Requirements (FR)
1. **Create Paste**: Users can paste plain text or code blocks (up to 10MB) and generate a unique URL key.
2. **Read Paste**: Anyone with the URL key can view the original text content.
3. **Custom Expiration & Access**: Users can set an expiration time (e.g., 1 hour, 1 day, 1 year, Never) and optional password protection.
4. **Anonymous & Registered Users**: Pastes can be uploaded anonymously or linked to an authenticated user account.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **High Availability**: 99.99% availability for paste read operations.
2. **Low Latency**: Read paste in `< 25ms`; create paste in `< 200ms`.
3. **Data Durability**: Stored pastes must never be corrupted or prematurely lost before expiration.
4. **Storage Scalability**: Efficiently store petabytes of immutable text blobs over a 5-year retention window.
