# 02 - Software Design Principles (DRY, KISS, YAGNI)

Software design principles are foundational guidelines that help developers write code that is clean, maintainable, and easy to extend.

---

## 1. DRY: Don't Repeat Yourself

> **Core Idea:** Every piece of knowledge or business logic must have a single, unambiguous, authoritative representation in the system.

### ❌ Bad Design (Violates DRY)
Duplicating the 18% tax calculation in multiple services (`CartService` and `InvoiceService`). Any change in the tax rate requires edits in multiple places.

### ✅ Good Design (Adheres to DRY)
Centralizing the tax logic into `TaxCalculator`.

```java
// Centralized calculation logic
class TaxCalculator {
    private static final double GST_RATE = 0.18;
    public static double calculateTax(double amount) {
        return amount * GST_RATE;
    }
}
```

### When NOT to use DRY:
- **Premature Abstraction:** Two blocks of code that look similar today might evolve differently tomorrow. Forcing them into one method creates accidental coupling.
- **Performance-Critical Code:** Extracting logic into abstractions or method calls might add slight indirection overhead or block compiler inlining in extreme high-throughput systems.
- **Sacrificing Readability:** If extracting code creates confusing, deeply nested wrappers, prefer clear code over DRY code.
- **Untested Legacy Code:** Modifying legacy code without test coverage just to remove duplication risks introducing regressions.

---

## 2. KISS: Keep It Simple, Stupid

> **Core Idea:** Simplicity should be the primary goal. Choose the simplest solution that works and avoid convoluted or over-engineered code.

### ❌ Bad Design (Violates KISS)
Using unnecessary nested `if-else` branches and state flags for a simple check.

### ✅ Good Design (Adheres to KISS)
A direct boolean expression.

```java
class DeliveryValidator {
    public static boolean isEligibleForFreeDelivery(double orderAmount, boolean isPrimeMember) {
        return isPrimeMember || orderAmount >= 500;
    }
}
```

---

## 3. YAGNI: You Aren't Gonna Need It

> **Core Idea:** Only implement what is actually required today. Never build features or abstractions just because you foresee a hypothetical future need.

### ❌ Bad Design (Violates YAGNI)
When asked for a basic note-taking feature (create and view notes), preemptively building tags, cloud synchronization, category hierarchies, and version history.

### ✅ Good Design (Adheres to YAGNI)
Creating a lightweight `Note` model and `NoteService` supporting only `addNote` and `getNotes`.

```java
class Note {
    private final String id;
    private final String content;
    public Note(String id, String content) {
        this.id = id;
        this.content = content;
    }
    public String getId() { return id; }
    public String getContent() { return content; }
}
```

### When NOT to use YAGNI:
- **Well-known / Confirmed Near-Future Requirements:** If a requirement is confirmed for the next sprint (e.g., image attachments in a messaging app), laying early architectural foundations saves heavy refactoring.
- **Performance-Critical Architecture:** Preemptively structuring data models to prevent architectural bottlenecks under expected scale.

---

## 🎯 Quick Summary

### 1. DRY (Don't Repeat Yourself)
- **Core Idea:** Every piece of knowledge or business logic must have a single, authoritative representation.
- **Code Demonstrates:** Centralizing tax calculation logic into `TaxCalculator` to prevent duplicating rates across cart and invoice services.
- **LLD Takeaway:** Avoid duplicating business logic across services; consolidate into reusable, single-source modules.
- **Memorable Rule:** *"Change once, update everywhere."*

### 2. KISS (Keep It Simple, Stupid)
- **Core Idea:** Simplicity should always be the primary goal; avoid convoluted or over-engineered solutions.
- **Code Demonstrates:** Replacing nested `if-else` flag checks with a direct, single-line boolean expression (`isPrimeMember || orderAmount >= 500`).
- **LLD Takeaway:** Write obvious, readable code that solves the problem without unnecessary state or branching complexity.
- **Memorable Rule:** *"The best code is the simplest code that works."*

### 3. YAGNI (You Aren't Gonna Need It)
- **Core Idea:** Implement only what is actually required today; never build speculative abstractions.
- **Code Demonstrates:** Building a minimal `Note` model with `addNote` and `getNotes` without unrequested tags, cloud sync, or version history.
- **LLD Takeaway:** Resist speculative architectural over-engineering; build for today's requirements while keeping code easy to evolve.
- **Memorable Rule:** *"Write code for today, design for tomorrow to change it."*

