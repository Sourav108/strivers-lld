# Staff-Level Requirements: News Feed Ranking System

## 📋 The Staff Prompt
*"Design a personalized real-time news feed ranking and delivery system (Meta / TikTok / Twitter style) serving 500 Million Daily Active Users, combining hybrid push-pull fan-out, multi-stage ML candidate ranking, and sub-100ms p99 load latency."*

---

## 🎯 Functional Requirements (FR)
1. **Publish Post**: Users publish text, image, and video content.
2. **Personalized Home Feed**: Return top 50 ranked, relevant posts from followed accounts and recommended creators.
3. **Real-Time Feed Updates**: Immediate visibility of breaking updates and interactions (likes, comments).

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Sub-100ms Feed Latency**: Candidate retrieval and ML scoring must finish within 80ms.
2. **Hybrid Fan-out**: Seamless handling of regular users vs celebrity/VIP accounts (100M+ followers).
3. **High Availability**: 99.99% availability globally.
