# When NOT to Use: Symmetric JWT Secret Sharing (HS256)

## ❌ Why Symmetric JWT Signing (HS256) is an Anti-Pattern at Scale:

1. **The Shared Secret Security Vulnerability**:
   - *Why*: Under HS256, every single microservice that needs to verify user tokens must possess the shared secret key. If a developer accidentally leaks the secret in one low-security logging service, the entire organization is compromised because anyone with the secret can forge administrative tokens.
   - *Staff-Level Decision*: **Mandate Asymmetric Cryptography (RS256 / Ed25519)**. The Auth Service holds the Private Key in an HSM, and all internal microservices verify tokens using cached public keys via standard JWKS endpoints (`/.well-known/jwks.json`).
