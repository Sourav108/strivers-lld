# 01 - Single Responsibility Principle (SRP)

> **Definition:** A class should have **one, and only one, reason to change**. It should have only one job, one responsibility, and one purpose.

---

## 💡 Real-Life Analogy
Imagine a chef who is forced to **cook, clean dishes, serve customers, and buy groceries**.
If the chef is overwhelmed with cleaning or buying groceries, cooking quality suffers.

**Solution:** Divide responsibilities among specialized roles:
- 👨‍🍳 **Chef:** Cooks food
- 🧹 **Cleaner:** Cleans dishes/kitchen
- 🧑‍💼 **Waiter:** Serves customers
- 📦 **Manager:** Orders groceries

---

## ❌ Bad Design (Violating SRP: "God Class")
A single `BadTUFCompiler` handles 5 distinct concerns:
1. Generating driver code
2. Syntax validation
3. Executing test cases
4. Database persistence
5. Output formatting

Any change to database schema, test harness, or UI format forces changes to the same monolithic compiler class, causing ripple effects.

---

## ✅ Good Design (Adhering to SRP)
Decompose the monolith into dedicated, single-purpose classes:

```java
// 1. Driver code generation only
class DriverCodeGenerator { ... }

// 2. Syntax validation only
class SyntaxChecker { ... }

// 3. Test execution only
class TestRunner { ... }

// 4. Persistence only
class DatabaseManager { ... }

// 5. User response formatting only
class UserOutputHandler { ... }

// 6. Workflow orchestration only
class CompilerCoordinator { ... }
```

---

## 🚀 Key Advantages of SRP
1. **Improved Maintainability:** Modifying one responsibility (e.g., swapping DB from Postgres to Mongo) doesn't risk breaking syntax checking or test execution.
2. **Enhanced Readability:** Small, focused classes are easy to understand at a glance.
3. **Better Reusability:** `SyntaxChecker` or `TestRunner` can be reused in other tools (e.g., IDE linter or CLI tester).
4. **Easier Testing:** Can test each component independently with simple unit tests and mocks.
5. **Lower Risk of Unintended Side Effects:** Isolated changes reduce bugs across unrelated features.

---

## ⚠️ Common Mistakes Violating SRP
- **Mixing Database Logic with Business Logic:** Embedding SQL/JDBC queries inside domain models or calculation services.
- **Coupling UI Logic with Business Logic:** Formatting HTML, CLI strings, or UI colors inside core algorithms.

---

## 📌 Is SRP Just for Classes?
**No.** SRP applies at all design levels:
- **Methods:** A method should do one thing well (e.g., `calculateSubtotal()` shouldn't also print to console).
- **Classes:** A class represents one coherent concept.
- **Modules / Packages:** Group related concerns together.
- **Microservices:** A service owns a single business capability (e.g., Auth Service vs. Payment Service).

---

## 🎯 Summary

| Property | Description |
|---|---|
| **Core Idea** | A class should have only one reason to change (single purpose). |
| **What the Code Demonstrates** | Refactoring a monolithic compiler into dedicated classes (`DriverCodeGenerator`, `SyntaxChecker`, `TestRunner`, `DatabaseManager`, `UserOutputHandler`, `CompilerCoordinator`). |
| **LLD Takeaway** | Separate concerns into focused classes and use a coordinator/orchestrator to stitch them together. |
| **One Memorable Rule** | *"One class, one responsibility, one reason to change."* |
