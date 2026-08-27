# Scale Follow-ups: Networking & Real-Time Protocols

## 🚀 1. What Changes at 10x Scale?
- **Connection Saturation**: Standard Linux file descriptor limits (`ulimit -n`) and Epoll event loop thread pools exhaust socket connections. Optimize OS kernel parameters (`sysctl net.ipv4.tcp_max_syn_backlog=65535`, `net.core.somaxconn=65535`).
- **TLS Handshake CPU Bottleneck**: TLS cryptographic handshakes consume server CPU cores. Offload TLS termination to hardware accelerators or specialized edge proxies (Envoy / Cloudflare).

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Global Edge Anycast**: Route client traffic to the nearest Point of Presence (PoP) across 300+ global edge locations via BGP Anycast.
- **TCP Termination at Edge**: Terminate TCP and TLS handshakes at the edge PoP (5ms RTT from user) and maintain long-lived warmed HTTP/2 / HTTP/3 connections across private fiber backbones to the origin datacenter, cutting dynamic API latency by 60%.
