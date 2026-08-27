# Capacity Estimation: YouTube / Netflix Video Streaming

## 🔢 1. Traffic & Streaming QPS Estimates

- **Assumptions**:
  - Daily Active Users (DAU): **500 Million**
  - Daily Videos Uploaded: **1 Million videos/day**
  - Average Raw Video Size: **500 MB**
  - Average Daily Video Watch Time per User: **30 minutes**

### Upload (Ingestion) QPS:
$$\text{Upload QPS} = \frac{10^6 \text{ videos}}{10^5 \text{ seconds}} = \mathbf{10 \text{ uploads/sec}}$$

### Daily Video Views:
$$\text{Daily Views} = 500\text{M} \times 5 \text{ videos/day} = \mathbf{2.5 \text{ Billion views/day}}$$
$$\text{Streaming Playback QPS} = \frac{2.5 \times 10^9}{10^5} = \mathbf{25,000 \text{ concurrent streams/sec}}$$

---

## 💾 2. Video Storage Estimation (5-Year Horizon)

- **Transcoding Multiplier**: Transcoding into 5 resolutions (4K, 1080p, 720p, 480p, 360p) across 2 codecs produces $\approx 3\times$ the original compressed video volume.
$$\text{Daily Storage} = 10^6 \times 500 \text{ MB} \times 3 = \mathbf{1.5 \text{ Petabytes (PB)/day}}$$

- **5-Year Video Storage**:
$$\text{5-Year Storage} = 1.5 \text{ PB} \times 365 \times 5 \approx \mathbf{2.73 \text{ Exabytes (EB)}}$$

---

## 🌐 3. Egress Bandwidth Requirements
- Average stream bitrate (1080p) $\approx \mathbf{5 \text{ Mbps}}$:
$$\text{Total Concurrent Bandwidth} = 500\text{M active} \times 10\% \text{ watching} \times 5 \text{ Mbps} = \mathbf{250 \text{ Terabits per second (Tbps)}}$$
*(99% of streaming bandwidth is served directly by distributed ISP Open Connect CDN appliances).*
