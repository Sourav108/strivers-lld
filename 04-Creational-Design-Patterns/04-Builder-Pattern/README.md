# 04 - Builder Design Pattern

> **Definition:** The Builder Pattern is a creational design pattern that constructs complex objects step by step. It separates the construction of a complex object from its representation, allowing the same construction process to produce different configurations and representations.

---

## 💡 Real-Life Analogy

### 🍔 Custom Burger & Pizza Orders
When ordering a custom burger at a gourmet restaurant:
- You specify mandatory components: **Bun type** and **Patty**.
- You optionally add customizations: **Cheese**, **Toppings** (lettuce, jalapeno, onions), **Sides** (fries), and **Beverage** (coke).
- The chef builds your exact meal step by step based on your instructions.
- Different customers use the exact same ordering pipeline to construct completely distinct meals.

---

## ❌ The Problem: Telescoping Constructor Anti-Pattern

In traditional constructor designs, handling optional fields leads to the **Telescoping Constructor Anti-Pattern**:

```java
class BurgerMeal {
    public BurgerMeal(String bun, String patty) { ... }
    public BurgerMeal(String bun, String patty, boolean cheese) { ... }
    public BurgerMeal(String bun, String patty, boolean cheese, List<String> toppings) { ... }
    public BurgerMeal(String bun, String patty, boolean cheese, List<String> toppings, String side) { ... }
    public BurgerMeal(String bun, String patty, boolean cheese, List<String> toppings, String side, String drink) { ... }
}
```

### Why this is problematic:
1. **Positional Confusion:** Easy to swap adjacent parameters of the same type (e.g. `String side` vs `String drink`).
2. **Ugly Null Passing:** Callers must pass `null` or dummy defaults: `new BurgerMeal("wheat", "veg", false, null, null, "water")`.
3. **Immutability Loss or Boilerplate:** Setters break immutability, while overloaded constructors create unmaintainable code.

---

## ✅ Good Design (Adhering to Builder Pattern)

```mermaid
classDiagram
    class BurgerMeal {
        -bunType: String
        -patty: String
        -hasCheese: boolean
        -toppings: List~String~
        -side: String
        -drink: String
        -BurgerMeal(builder: BurgerBuilder)
        +toString() String
    }

    class BurgerBuilder {
        -bunType: String
        -patty: String
        -hasCheese: boolean
        -toppings: List~String~
        -side: String
        -drink: String
        +BurgerBuilder(bunType: String, patty: String)
        +withCheese(hasCheese: boolean) BurgerBuilder
        +withToppings(toppings: List~String~) BurgerBuilder
        +withSide(side: String) BurgerBuilder
        +withDrink(drink: String) BurgerBuilder
        +build() BurgerMeal
    }

    BurgerMeal +-- BurgerBuilder : Static Nested Class
    BurgerBuilder ..> BurgerMeal : Instantiates
```

---

## 🔍 Constructor Approach vs. Builder Pattern

| Aspect | Telescoping Constructor | Builder Pattern |
|---|---|---|
| **Readability** | ❌ Poor (confusing parameter order & `null`s) | ✅ Excellent (fluent, self-documenting method calls) |
| **Flexibility** | ❌ Low (rigid parameter combinations) | ✅ High (configure only desired options) |
| **Immutability** | ⚠️ Difficult without massive constructors | ✅ Guaranteed (fields are `final` in target class) |
| **Safety** | ❌ High risk of `NullPointerException` & type bugs | ✅ Compile-time safe step-by-step validation |

---

## 🌍 Real-World Industry Examples

1. **Lombok `@Builder`:** Automatically generates static inner builder classes at compile-time.
2. **Java Core API:** `java.lang.StringBuilder`, `java.net.http.HttpRequest.newBuilder()`.
3. **E-Commerce Cart Systems (Amazon):** Assembling cart items with variable quantities, gift wraps, delivery tiers, and discount codes.

---

## 🎯 When to Use & When to Avoid

### ✅ When to Use:
- When an object has **many fields** (especially $\ge 4$ optional fields).
- When you want to ensure the built object is **immutable** (`final` fields without setters).
- When object construction requires **step-by-step validation** before creation.

### ❌ When to Avoid:
- When the class has only **1–2 simple fields**.
- When the object is completely mutable and simple constructors/setters suffice.

---

### 🎯 Quick Summary

* **Core Idea:** Separate the step-by-step construction of a complex object from its final representation using a fluent builder interface.
* **Code Demonstrates:** Refactoring telescoping constructors into a static nested `BurgerBuilder` with chainable `withXYZ()` methods and an immutable `BurgerMeal`.
* **LLD Takeaway:** Use the Builder Pattern whenever an entity has multiple optional attributes or requires immutability without parameter confusion.
* **Memorable Rule:** *"Mandatory fields in the Builder constructor, optional fields in fluent chain methods, final creation in `.build()`."*
