# 11 — Security & Rate Limiting

## 🛡️ 1. Authentication (AuthN) vs Authorization (AuthZ)

```mermaid
flowchart TD
    Request["Incoming Request"] --> AuthN["1. Authentication (AuthN)<br/>Who are you?<br/>(Passkeys, OAuth2, JWT)"]
    AuthN -->|Identity OK| AuthZ["2. Authorization (AuthZ)<br/>What can you do?<br/>(RBAC, ABAC, Scopes)"]
    AuthZ -->|Access OK| Protected["Protected Resource"]
```

---

## 🔑 2. OAuth 2.0 Flow with PKCE & JSON Web Tokens (JWT)

```mermaid
sequenceDiagram
    autonumber
    actor User as End User
    participant App as Client App (SPA / Mobile)
    participant Auth0 as Auth Server (OAuth 2.0)
    participant API as Resource API Gateway

    User->>App: Click "Login with Google"
    App->>Auth0: Redirect to /authorize (code_challenge)
    Auth0->>User: Display Login & Consent
    User-->>Auth0: Authenticates (Credentials + 2FA)
    Auth0-->>App: Redirect with Auth Code
    App->>Auth0: POST /token (Code + code_verifier)
    Auth0-->>App: Return Access Token (JWT)
    App->>API: GET /v1/profile (Bearer JWT)
    Note over API: Verifies JWT signature locally via JWKS
    API-->>App: 200 OK (User Data)
```

### Anatomical Structure of a JWT:
$$\mathbf{\text{JWT}} = \underbrace{\text{Base64(Header)}}_{\text{Algorithm: RS256}} \,.\, \underbrace{\text{Base64(Payload)}}_{\text{sub, exp, iss, roles}} \,.\, \underbrace{\text{Signature}}_{\text{Encrypted Hash with Private Key}}$$

- **RS256 (Asymmetric)**: Auth server signs token with Private Key; API Gateways verify using cached Public Key without calling Auth DB!

---

## 🚦 3. Rate Limiting Algorithms Deep Dive

Rate limiting protects APIs from abuse, credential stuffing, and DDoS outages.

```mermaid
flowchart TD
    Algorithms["Rate Limiting Algorithms"]
    
    subgraph BucketBased["Bucket Approaches"]
        TB["1. Token Bucket<br/>(Refills steady, allows bursts)"]
        LB["2. Leaky Bucket<br/>(Smooth constant outflow)"]
    end

    subgraph WindowBased["Window Approaches"]
        FW["3. Fixed Window<br/>(Prone to boundary spikes)"]
        SWL["4. Sliding Window Log<br/>(Exact timestamps in ZSET)"]
        SWC["5. Sliding Window Counter<br/>(Hybrid formula: Cloudflare / Envoy)"]
    end

    Algorithms --> BucketBased
    Algorithms --> WindowBased
```

### Algorithm Comparison Matrix:

| Algorithm | Handles Bursts? | Memory Footprint | Accuracy | Industry Standard Use Case |
|---|---|---|---|---|
| **Token Bucket** | ✅ Yes (Up to bucket capacity) | 🟢 $O(1)$ (Tokens + Last Refill Time) | High | AWS API Gateway, Stripe API |
| **Leaky Bucket** | ❌ No (Smooths to fixed rate) | 🟡 $O(\text{Queue Size})$ | High | Traffic shaping, E-commerce checkouts |
| **Fixed Window** | ❌ No ($2\times$ burst at edges) | 🟢 $O(1)$ (Simple integer counter) | 🔴 Low (Edge spike vulnerability) | Simple non-critical rate limits |
| **Sliding Window Counter**| ✅ Yes | 🟢 $O(1)$ (Only 2 numbers: Prev & Curr count)| 🟢 $99.9\%$ | Cloudflare, Envoy, Redis Lua scripts |

---

## 🔒 4. SSL/TLS & Mutual TLS (mTLS)

```mermaid
sequenceDiagram
    autonumber
    participant Client as Client Service A
    participant Server as Server Service B
    
    Note over Client,Server: TLS 1.3 Handshake (Single Round-Trip)
    Client->>Server: ClientHello (Supported Ciphers + Key Share)
    Server-->>Client: ServerHello + Server Certificate + CertificateRequest
    Client->>Client: Verifies Server Certificate against Root CA
    Client->>Server: Client Certificate (mTLS) + Finished
    Server->>Server: Verifies Client Certificate against Internal CA
    Server-->>Client: Finished
    Note over Client,Server: Zero-Trust Encrypted Tunnel Established
```

- **mTLS (Mutual TLS)**: Both client and server authenticate each other using X.509 digital certificates, ensuring Zero-Trust internal microservice communication.
