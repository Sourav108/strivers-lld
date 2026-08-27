# When NOT to Use: Heavy RFC Bureaucracy

## ❌ When Heavy RFC Governance is an Anti-Pattern:

1. **Small 1-Sprint Bug Fixes & Routine Features**:
   - *Why*: Forcing engineers to write a 10-page RFC to add a single database index or a minor frontend button wastes weeks of developer time.
   - *Staff Rule*: **Lightweight 1-pager design docs** for small features; reserve full RFC reviews for cross-team interfaces, new database adoptions, and core data model changes.
2. **Exploratory Proof-of-Concepts (PoCs)**:
   - *Why*: Requiring consensus before building an experimental prototype kills innovation. Let teams hack a disposable prototype first to gather empirical data before writing the formal RFC.
