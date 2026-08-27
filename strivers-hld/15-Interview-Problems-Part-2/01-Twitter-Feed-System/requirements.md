# Requirements: Design Twitter / News Feed System

## 📋 Functional Requirements (FR)
1. **Post Tweet**: Users can publish tweets (text up to 280 chars, with optional media attachments).
2. **Follow Users**: Users can follow/unfollow other users.
3. **Home Timeline**: Users can view a reverse-chronological feed of tweets published by people they follow.
4. **User Timeline**: Users can view their own profile timeline of tweets.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Low Latency Feed Generation**: Home timeline must load in **`< 100ms`** (p99).
2. **High Availability**: 99.99% availability for feed viewing and tweet posting.
3. **Eventual Consistency**: It is acceptable if a follower sees a new tweet with a 1–2 second delay.
4. **Massive Scale**: Support **300 Million Daily Active Users (DAU)**.
