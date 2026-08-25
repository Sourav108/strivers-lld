# 05 - Dependency Inversion Principle (DIP)

> **Definition:** 
> 1. High-level modules should not depend on low-level modules. Both should depend on **abstractions**.
> 2. Abstractions should not depend on details. Details should depend on **abstractions**.

Rather than high-level business classes directly instantiating and depending on low-level implementation details, both layers interact through interfaces or abstract contracts.

---

## 🔑 Key Concepts & Pre-requisites

| Term | Role | Real-Life Analogy | Software Example |
|---|---|---|---|
| **High-Level Modules** | Core business logic and decision makers (the brains of the system). | 🧑‍💼 **CEO / Manager** (decides strategy, coordinates goals) | `RecommendationEngine`, `OrderService`, `PaymentProcessor` |
| **Low-Level Modules** | Implementation details (grunt work: database, network, disk, APIs). | 👷 **Employees / Contractors** (perform specific tasks) | `PostgresDatabase`, `TrendingAlgorithm`, `StripeClient` |
| **Abstractions** | Contracts and interfaces defining *what* is done, not *how*. | 📋 **Job Contract / Specification** | `RecommendationStrategy`, `UserRepository`, `PaymentGateway` |

---

## 💡 Real-Life Analogy

### 🍕 Food Delivery App
When you are hungry and want pizza:
- **You (High-level module):** Place an order through the **Food Delivery App interface (Abstraction)**.
- **The App:** Coordinates with the **Restaurant / Chef (Low-level module)** to prepare and deliver the food.
- You don't need to know which specific oven or chef is making the pizza, nor do you talk to them directly.
- Both you and the restaurant rely on the standardized delivery platform contract.

---

## ❌ Bad Design (Violating DIP: Tightly Coupled)

The high-level `BadRecommendationEngine` directly creates and depends on a concrete low-level class (`BadRecentlyAdded`):

```java
class BadRecentlyAdded {
    public void getRecommendations() {
        System.out.println("Showing recently added content...");
    }
}

// ❌ High-level module depends directly on low-level detail
class BadRecommendationEngine {
    private BadRecentlyAdded recommender = new BadRecentlyAdded();

    public void recommend() {
        recommender.getRecommendations(); // Hardcoded dependency!
    }
}
```

### Why this is bad:
- ⚠️ **Rigid Coupling:** Cannot switch to `TrendingNow` or `GenreBased` algorithms without editing the `BadRecommendationEngine` source code.
- ⚠️ **Untestable:** Cannot mock the recommendation algorithm during unit testing.
- ⚠️ **Violates OCP & DIP:** High-level policy is held hostage by low-level implementation changes.

---

## ✅ Good Design (Adhering to DIP: Inverted Dependency)

Invert the dependency so both layers depend on the `RecommendationStrategy` abstraction:

```
┌─────────────────────────┐
│  RecommendationEngine   │ (High-Level Module)
└────────────┬────────────┘
             │ depends on
             ▼
┌─────────────────────────┐
│ <<RecommendationStrategy│ (Abstraction)
│   + getRecommendations()│
└────────────▲────────────┘
             │ implemented by
     ┌───────┼─────────────────────────┐
     │                                 │
┌────────────┴────────────┐   ┌────────┴────────────────┐
│   TrendingNowStrategy   │   │   GenreBasedStrategy    │ (Low-Level Modules)
└─────────────────────────┘   └─────────────────────────┘
```

### Implementation:

```java
// 1. Abstraction (Contract)
interface RecommendationStrategy {
    void getRecommendations();
}

// 2. Low-Level Concrete Implementations
class RecentlyAdded implements RecommendationStrategy {
    @Override
    public void getRecommendations() {
        System.out.println("🎬 [Recently Added] New releases this week: Inception, Interstellar");
    }
}

class TrendingNow implements RecommendationStrategy {
    @Override
    public void getRecommendations() {
        System.out.println("🔥 [Trending Now] Top 10 shows today: Stranger Things, Breaking Bad");
    }
}

class GenreBased implements RecommendationStrategy {
    @Override
    public void getRecommendations() {
        System.out.println("🍿 [Genre Based] Because you watched Sci-Fi: Dark, Black Mirror");
    }
}

// 3. High-Level Module depending only on Abstraction
class RecommendationEngine {
    private RecommendationStrategy strategy;

    // Dependency Injection via constructor
    public RecommendationEngine(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    // Dynamic strategy switching at runtime
    public void setStrategy(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public void recommend() {
        strategy.getRecommendations();
    }
}
```

---

## 🚀 Key Advantages of DIP

1. **Flexibility:** Easily swap algorithms or backend databases without touching core business logic.
2. **Superior Testability:** High-level classes can be tested in isolation using mock abstractions.
3. **Reusability & Decoupling:** Modules are independent and can be reused in different workflows or applications.
4. **Maintainability & Scalability:** Upgrading a low-level algorithm does not risk breaking high-level orchestration code.

---

## 🎯 When to Apply DIP?

- When building business services that interact with external systems (databases, caches, third-party payment gateways, messaging queues).
- When a high-level component uses `new ConcreteClass()` internally, tightly coupling itself to an implementation.
- When you anticipate multiple implementations or need runtime algorithm switching (e.g. Strategy pattern).
- When writing unit tests requiring mock dependencies.

---

### 🎯 Quick Summary

* **Core Idea:** High-level business logic and low-level details must both depend on abstractions, never on concrete classes.
* **Code Demonstrates:** Refactoring a hardcoded `RecommendationEngine` to accept interchangeable `RecommendationStrategy` implementations via Dependency Injection.
* **LLD Takeaway:** Invert direct dependencies by introducing interfaces, enabling runtime flexibility, mockable testing, and modular architectures.
* **Memorable Rule:** *"Depend on abstractions (interfaces), not on concretions (classes)."*
