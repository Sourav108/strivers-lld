# 03 - Liskov Substitution Principle (LSP)

> **Definition:** If $S$ is a subtype of $T$, then objects of type $T$ may be replaced with objects of type $S$ without altering the correctness of the program.
> 
> *Subclasses must be substitutable for their base classes without breaking client expectations, contracts, or program behavior.*

---

## 💡 Real-Life Analogy

### 🏨 The Pet Hotel
Imagine a pet hotel designed under the general contract: *"Any pet staying here can be fed pet food, walked outside, and groomed."*
- **Valid Substitution (Hamster / Dog / Cat):** Fits the expected pet care routines without breaking the hotel's workflow.
- **LSP Violation (Pet Snake):** 
  - Cannot be walked.
  - Cannot be groomed.
  - Rejects pet food (requires live mice).
  - Breaks the hotel's fundamental assumptions.

If substituting a subtype breaks the expectations established by the parent type, **LSP is violated**.

---

## ❌ Bad Design (Violating LSP: The Rectangle-Square Problem)

In mathematics, a Square *is a* Rectangle. However, in OOP, making `Square` inherit from `Rectangle` breaks behavioral subtyping:

```java
class Rectangle {
    protected int width, height;

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getArea() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // Violates parent contract
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
        this.width = height; // Violates parent contract
    }
}
```

### Why does this break client code?
```java
void resizeAndCheckArea(Rectangle r) {
    r.setWidth(5);
    r.setHeight(10);
    // Client expects area = 5 * 10 = 50
    // If r is Square: setHeight(10) sets width=10, area becomes 100!
    assert r.getArea() == 50 : "LSP Violation!";
}
```

---

## ✅ Good Design (Adhering to LSP)

### 1. Shape Hierarchy (Separating Incompatible Contracts)
Instead of forcing a mutable inheritance relationship between Square and Rectangle, make them independent classes adhering to a shared `Shape` contract:

```java
interface Shape {
    int getArea();
}

class Rectangle implements Shape {
    private final int width;
    private final int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getArea() { return width * height; }
}

class Square implements Shape {
    private final int side;

    public Square(int side) {
        this.side = side;
    }

    @Override
    public int getArea() { return side * side; }
}
```

### 2. Backend Notification Pipeline (True Behavioral Subtyping)
Every notification channel (`EmailNotification`, `SMSNotification`, `PushNotification`) honors the exact contract of `Notification`:

```
                    ┌─────────────────────────┐
                    │      Notification       │
                    ├─────────────────────────┤
                    │ + send(recipient, msg)  │
                    └────────────▲────────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         │                       │                       │
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│EmailNotification │    │ SMSNotification  │    │PushNotification  │
└──────────────────┘    └──────────────────┘    └──────────────────┘
```

---

## 🔍 How to Spot LSP Violations

Ask yourself these key questions during design and code reviews:
1. **Contract Mutation:** Does the subclass override methods in a way that alters preconditions or postconditions?
2. **Empty / No-op Overrides:** Does a subclass leave methods blank or throw `UnsupportedOperationException` (e.g. `ReadOnlyAccount` extending `Account` and throwing on `deposit()`)?
3. **Type Checking in Client Code:** Does client code use `instanceof` or type casts to handle specific subclasses differently?
4. **Altered Return Values:** Does the child return values outside the expected contract of the parent?

---

## 🛠 Best Practices to Avoid LSP Violations

- **Favor Composition Over Inheritance:** Use inheritance only for true *is-substitutable-for* relationships, not just code reuse.
- **Design by Contract:** Base classes/interfaces define promises (preconditions, postconditions, invariants). Subtypes must uphold all of them.
- **Keep Subtypes Behavioral:** A subtype should only extend capabilities, never restrict or remove behavior expected by callers.

---

## 🎯 Quick Summary

- **Core Idea:** Subtypes must be seamlessly substitutable for their base types without breaking client expectations or program correctness.
- **Code Demonstrates:** ❌ Mutator mutation in `Square extends Rectangle` breaking area calculations vs. ✅ polymorphic `Shape` and `Notification` hierarchies that guarantee substitutability.
- **LLD Takeaway:** Model inheritance around behavioral contracts rather than real-world taxonomies; if a subclass cannot honor all parent operations, use composition or separate interfaces.
- **One Memorable Rule:** *"If it looks like a duck and quacks like a duck but needs batteries, you have the wrong abstraction."*
