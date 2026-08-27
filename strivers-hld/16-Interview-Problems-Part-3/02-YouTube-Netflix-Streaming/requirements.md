# Requirements: Design YouTube / Netflix Video Streaming

## 📋 Functional Requirements (FR)
1. **Video Ingestion & Upload**: Creators can upload high-resolution video files (up to 4K/8K, several gigabytes).
2. **Video Transcoding & Chunking**: Encode raw videos into multiple formats (H.264, VP9, AV1) and resolutions (1080p, 720p, 480p, 360p) split into small chunks.
3. **Adaptive Bitrate Video Streaming**: Stream video smoothly to clients via HLS / DASH protocols based on fluctuating client network bandwidth.
4. **Search & Metadata**: Search videos by title/tags and view channel statistics (views, likes).

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Low Playback Startup Latency & Zero Buffering**: First frame loads in **`< 1 second`**.
2. **Massive Video Delivery Bandwidth**: Deliver **Petabits of video traffic per second** globally.
3. **High Availability**: 99.99% playback availability worldwide.
4. **High Storage Durability**: 99.999999999% durability on original master video files.
