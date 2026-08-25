# 02 - Open/Closed Principle (OCP)

> **Definition:** Software entities (classes, modules, functions, etc.) should be **open for extension, but closed for modification**.

This means that the behavior of a module can be extended without modifying its source code. The goal is to reduce the risk of breaking existing, tested functionality when requirements change.

---

## 💡 Real-Life Analogy

### 🔌 Travel Power Adapters
Imagine you travel from India to the UK. Your Indian charger doesn't fit into UK power sockets.
- Instead of cutting wires or buying a completely new charger, you use a **travel adapter**.
- The adapter **extends** your existing charger's usability to work in the UK.
- You did **not modify** the internal circuit or hardware of the original charger.

Similarly, in code, OCP encourages adding new functionality via **extension** (new classes/interfaces), rather than altering existing, stable code.

---

## 🌍 Real-World Example: Invoicing & Tax Calculation System

As an invoicing system grows, it must handle tax rules for different regions:
- 🇮🇳 **India:** GST 18%
- 🇺🇸 **US:** Sales Tax 8%
- 🇬🇧 **UK:** VAT 12%
- 🇩🇪 **Germany (New):** 15%

---

## ❌ Bad Design (Violating OCP)

Using conditional `if-else` or `switch` statements inside the processor class:

```java
class InvoiceProcessor {
    public double calculateTotal(String region, double amount) {
        if (region.equalsIgnoreCase("India")) {
            return amount + amount * 0.18;
        } else if (region.equalsIgnoreCase("US")) {
            return amount + amount * 0.08;
        } else if (region.equalsIgnoreCase("UK")) {
            return amount + amount * 0.12;
        } else {
            return amount; // No tax for unknown region
        }
    }
}
```

### Why is this bad?
- ⚠️ **Modification Required:** Adding a new region (e.g., Germany) requires directly editing `calculateTotal()`.
- ⚠️ **High Regression Risk:** You risk breaking existing calculations for India/US/UK while modifying code for Germany.
- ⚠️ **Poor Maintainability & Scalability:** As more countries/rules are added, the class turns into a giant conditional monster that is difficult to test and maintain.

---

## ✅ Good Design (Adhering to OCP)

Use polymorphism and the Strategy pattern with Dependency Injection:

```
                  ┌──────────────────────┐
                  │   <<interface>>      │
                  │   TaxCalculator      │
                  ├──────────────────────┤
                  │ + calculateTax(amt)  │
                  └──────────▲───────────┘
                             │
       ┌─────────────────────┼─────────────────────┬─────────────────────┐
       │                     │                     │                     │
┌──────────────┐      ┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│   IndiaTax   │      │    USTax     │      │    UKTax     │      │  GermanyTax  │
│  Calculator  │      │  Calculator  │      │  Calculator  │      │  Calculator  │
└──────────────┘      └──────────────┘      └──────────────┘      └──────────────┘
```

### Implementation Breakdown:

1. **Tax Strategy Interface:**
```java
interface TaxCalculator {
    double calculateTax(double amount);
}
```

2. **Region-Specific Implementations:**
```java
class IndiaTaxCalculator implements TaxCalculator {
    public double calculateTax(double amount) {
        return amount * 0.18; // GST
    }
}

class USTaxCalculator implements TaxCalculator {
    public double calculateTax(double amount) {
        return amount * 0.08; // Sales Tax
    }
}

class UKTaxCalculator implements TaxCalculator {
    public double calculateTax(double amount) {
        return amount * 0.12; // VAT
    }
}
```

3. **Client Using Dependency Injection (Closed for Modification):**
```java
class Invoice {
    private double amount;
    private TaxCalculator taxCalculator;

    public Invoice(double amount, TaxCalculator taxCalculator) {
        this.amount = amount;
        this.taxCalculator = taxCalculator;
    }

    public double getTotalAmount() {
        return amount + taxCalculator.calculateTax(amount);
    }
}
```

4. **Extending with Germany (No Changes to Existing Code):**
```java
// Open for extension: simply add a new class!
class GermanyTaxCalculator implements TaxCalculator {
    public double calculateTax(double amount) {
        return amount * 0.15;
    }
}
```

---

## 🎯 When to Apply OCP?

- **Evolving Business Rules:** When a module is expected to change due to shifting business requirements (e.g. payment gateways, discount algorithms, notification channels).
- **Extending Without Risk:** When there is a need to extend functionality without touching or re-testing existing, stable code.
- **Frameworks & Plugins:** When developing extensible systems like billing engines, tax calculators, or plugin architectures.
- **Safeguarding Production Code:** When aiming to protect core, battle-tested modules from regression bugs.
- **Eliminating God Classes:** When a class grows too large with branching logic (`if-else` / `switch`), signaling the need to extract behaviors into polymorphic components.

> [!NOTE]
> Applying OCP preemptively without clear extension needs can introduce unnecessary abstraction. It is most effective when applied in response to observed patterns of change or anticipated scalability.

---

## 🧠 Common Misconceptions about OCP

| Misconception | Reality |
|---|---|
| *"Code should never be changed again."* | OCP emphasizes avoiding changes to **core stable logic** while allowing behavior to be extended safely through abstractions. |
| *"OCP leads to too many classes (overkill)."* | While it introduces more interfaces/classes, the trade-off greatly improves modularity, isolation, and unit testability. |
| *"OCP makes code harder to read."* | For trivial code, abstractions may add boilerplate; but in complex/growing domains, it drastically improves clarity by eliminating nested conditionals. |
| *"OCP must always be applied upfront."* | Preemptive abstraction can lead to over-engineering. Apply OCP when requirements reveal variance or frequent extensions. |
| *"Refactoring violates OCP."* | Refactoring is not a violation; it is often the exact step taken to make legacy code compliant with OCP. |
| *"OCP makes retesting legacy code unnecessary."* | Stable core code remains untouched, but new extension classes and integrations must always be tested thoroughly. |

---

## 🎯 Quick Summary

- **Core Idea:** Software entities should be open for extension, but closed for modification.
- **Code Demonstrates:** Replacing hardcoded `if-else` country tax checks with interchangeable `TaxCalculator` strategies injected into `Invoice` via Dependency Injection.
- **LLD Takeaway:** Protect stable core classes from regressions by allowing new behaviors to be introduced through polymorphic extensions rather than modifying existing code.
- **Memorable Rule:** *"Write code that allows you to add new features by writing new code, not by changing old code."*

