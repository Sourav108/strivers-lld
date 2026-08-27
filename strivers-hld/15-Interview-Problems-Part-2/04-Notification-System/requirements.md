# Requirements: Design a Distributed Notification System

## 📋 Functional Requirements (FR)
1. **Multi-Channel Delivery**: Support sending notifications via **Mobile Push (APNS/FCM)**, **SMS (Twilio)**, and **Email (SendGrid/SES)**.
2. **User Preferences & Opt-Out**: Respect user notification settings (e.g. disable marketing emails, enable transactional SMS).
3. **Priority Queuing**: Critical transactional notifications (e.g. OTP, 2FA, fraud alerts) must bypass marketing/promotional queues.
4. **Rate Limiting & De-duplication**: Prevent spamming users with duplicate alerts within short intervals.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **High Throughput**: Process **100 Million notifications per day**.
2. **Low Latency for High-Priority Alerts**: OTP / 2FA SMS delivered in **`< 5 seconds`**.
3. **At-Least-Once Delivery**: No notification should be silently dropped.
4. **Extensibility**: Easily add new delivery vendors and channels via pluggable adapters.
