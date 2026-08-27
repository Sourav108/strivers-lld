# When NOT to Use: Custom In-House Infrastructure (The "Not Invented Here" Syndrome)

## ❌ The "Not Invented Here" (NIH) Trap:

1. **Building In-House Authentication / Identity Management**:
   - *Why*: Writing your own OAuth2/OIDC provider, password salting, passkeys, and MFA requires dedicated full-time security and cryptography engineers. A single bug can result in severe data breaches.
   - *Better Choice*: Adopt **Auth0 / Okta / AWS Cognito / Clerk**.
2. **Building Custom Kubernetes / Object Storage on Day 1**:
   - *Why*: Operating self-hosted Ceph or MinIO clusters requires dedicated 24/7 SRE on-call rotations for hardware disk swaps and scrub repairs.
   - *Better Choice*: **Managed Cloud Services (AWS S3, Google GCS)** until storage scale exceeds Petabytes.
