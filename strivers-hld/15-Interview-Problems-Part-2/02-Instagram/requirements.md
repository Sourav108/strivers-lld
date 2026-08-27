# Requirements: Design Instagram (Photo & Video Sharing)

## 📋 Functional Requirements (FR)
1. **Upload Media**: Users can upload high-resolution photos and short videos with captions.
2. **View Feed & Profiles**: Users can view the photo feed of accounts they follow, and browse individual user profile grids.
3. **Follow & Social Graph**: Users can follow and unfollow other accounts.
4. **Like & Comment**: Users can like and comment on posts.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Ultra-Low Latency Media Delivery**: Images and videos must load smoothly without buffering via Global CDNs.
2. **High Availability**: 99.99% availability for viewing feeds and photos.
3. **High Durability**: Uploaded media must never be corrupted or lost (99.999999999% durability).
4. **Massive Scale**: Support **500 Million Daily Active Users (DAU)**.
