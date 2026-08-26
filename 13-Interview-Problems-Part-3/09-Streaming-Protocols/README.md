# Streaming Protocols in Low-Level Design

Delivering audio and video content reliably with low initial buffering latency requires choosing the right streaming protocol.

---

## 1. Comparison of Streaming Approaches

| Protocol | Transport | Mechanism | Pros | Cons | Best For |
|---|---|---|---|---|---|
| **HTTP Range Requests** | HTTP / TCP | Client requests byte ranges (`bytes=0-1048575`); server responds with `HTTP 206 Partial Content`. | **Simple, zero encoding overhead**, native seeking, universally supported by CDNs and browsers. | Client must manage buffering; no dynamic bitrate switching. | Music streaming platforms (Spotify, Apple Music, SoundCloud). |
| **HLS (HTTP Live Streaming)** | HTTP / TCP | Audio/Video split into small media segments (~6–10s `.ts` files) indexed by `.m3u8` playlists. | **Adaptive Bitrate Streaming (ABR)**, error resilience, live and on-demand support. | Higher latency (6–30s); segment generation overhead. | Video streaming (Netflix, YouTube), Live TV broadcasts. |
| **DASH (Dynamic Adaptive Streaming over HTTP)** | HTTP / TCP | XML Media Presentation Description (`.mpd`) manifest with segmented media chunks. | Open standard (codec agnostic), multi-audio/subtitle track switching. | Higher complexity to configure and maintain. | Cross-platform video platforms, OTT services. |
| **WebSockets / WebRTC** | TCP / UDP | Full-duplex persistent bidirectional streaming. | Ultra-low latency ($< 500\text{ms}$). | High server resource footprint; cannot easily leverage edge CDNs. | Live interactive audio rooms (Clubhouse, Discord), Video calls. |

---

## 2. HTTP Range Requests (Deep Dive)

### Request Flow
```
Client:
  GET /api/stream/SONG-01 HTTP/1.1
  Range: bytes=0-1048575

Server:
  HTTP/1.1 206 Partial Content
  Content-Range: bytes 0-1048575/5242880
  Content-Length: 1048576
  Content-Type: audio/mpeg

  [Audio binary payload...]
```

### In-Memory Caching Strategy
- Cache key: `chunk_{songId}_{start}_{end}`
- Eviction policy: **Least Recently Used (LRU)**
- Audio chunk size: **1 MB (1,048,576 bytes)**

---

## 3. Summary & Interview Takeaways
- For **Audio / Music Streaming LLD**: Recommend **HTTP Range Requests** with **LRU Caching** for simplicity, fast seeking, and instant playback start.
- For **Video Streaming / Live Broadcasts LLD**: Recommend **HLS / DASH** for dynamic adaptive bitrate adjustments under fluctuating network bandwidth.
