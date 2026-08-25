# 01 - Dependency Injection (DI)

## Core Idea

**Dependency Injection (DI)** is a software design technique where an object receives its collaborators and dependencies from an external source (**Composition Root** or **IoC Container**) rather than creating them directly internally using the `new` keyword. By separating object creation from business logic execution and programming to abstractions (interfaces), DI achieves loose coupling, seamless component swappability, and effortless unit testing with mock objects.

---

## 💡 Real-Life Analogy

### 🍳 The Executive Chef & Ingredient Supplier
- **❌ Without DI (Growing Ingredients in the Kitchen):** A chef who wants to cook a pasta dish must step out of the kitchen, plant wheat, grow tomatoes, and milk cows to produce cheese before cooking. If the recipe changes to vegan pasta, the chef has to build a whole new farm.
- **✅ With DI (Supplier Delivery):** The chef specifies the contracts needed (*"I need tomatoes and cheese"*). An external food supplier (the Injector) delivers the fresh ingredients directly to the kitchen counter. The chef can effortlessly swap dairy cheese for vegan cashew cheese without altering the cooking process.

---

## 🏗️ The 3 Types of Dependency Injection

```
                                 +-------------------------+
                                 |   Composition Root /    |
                                 |      IoC Container      |
                                 +-------------------------+
                                    /         |         \
                                   /          |          \
                 1. Constructor   /    2. Setter |     3. Interface
                    Injection   /      Injection |       Injection
                               v              v              v
                     [OrderService]     [OrderService]     [OrderService]
                     - final fields     - mutable fields   - implements Injectable
```

| DI Type | How It Works | Immutability | Null-Safety | Best Used For |
|---|---|---|---|---|
| **1. Constructor Injection** *(Recommended)* | Passed via constructor params. | ✅ **Yes (`final`)** | ✅ **Guaranteed Complete** | Mandatory dependencies, thread-safe domain services. |
| **2. Setter Injection** | Passed via `setService()` methods. | ❌ No (Mutable) | ⚠️ Risk of uninitialized state | Optional dependencies, runtime reconfigurations. |
| **3. Interface Injection** | Injected via custom interface method. | ❌ No | ⚠️ Verbose | Rare legacy systems; generally avoided in modern Java. |

---

## ❌ Bad Design (Hardcoded Dependencies & Hidden Coupling)

```java
class OrderService {
    // ❌ Tightly coupled to concrete implementations using 'new'
    private InventoryService inventory = new InventoryService();
    private PaymentService payment = new RazorpayPayment();
    private NotificationService notification = new EmailNotificationService();

    public void checkout(Order order) {
        inventory.blockItems(order);
        payment.process(order.getAmount());
        notification.send("Order confirmed!");
    }
}
```

### What is wrong?
- ⚠️ **Impossible Unit Testing:** Cannot test `checkout()` without triggering real payment transactions and sending live customer emails.
- ⚠️ **Violates Open/Closed Principle (OCP):** Switching from Razorpay to Stripe forces modifications and re-compilations inside `OrderService`.
- ⚠️ **Violates Dependency Inversion Principle (DIP):** High-level business logic directly depends on low-level concrete utility classes.

---

## ✅ Good Design (Adhering to Constructor Dependency Injection)

Decouple business logic by depending on interfaces and injecting dependencies at construction time:

```java
// 1. Abstraction Contracts
interface PaymentGateway { void processPayment(double amount); }
interface NotificationService { void send(String message); }
interface InventoryService { boolean reserve(String itemId, int qty); }

// 2. Concrete Implementations
class StripePaymentGateway implements PaymentGateway {
    @Override public void processPayment(double amount) {
        System.out.println("💳 [Stripe Gateway] Charged ₹" + amount);
    }
}

class SMSNotificationService implements NotificationService {
    @Override public void send(String msg) {
        System.out.println("📱 [SMS] Notification sent: " + msg);
    }
}

// 3. High-Level Service with Constructor Injection
class OrderService {
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;
    private final InventoryService inventoryService;

    // Injected up-front: Guarantees complete initialization & thread safety
    public OrderService(PaymentGateway paymentGateway, 
                        NotificationService notificationService, 
                        InventoryService inventoryService) {
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.inventoryService = inventoryService;
    }

    public void checkout(String itemId, int qty, double amount) {
        if (inventoryService.reserve(itemId, qty)) {
            paymentGateway.processPayment(amount);
            notificationService.send("Order placed for " + itemId);
        }
    }
}

// 4. Composition Root (The only place where 'new' is called)
class Main {
    public static void main(String[] args) {
        PaymentGateway payment = new StripePaymentGateway();
        NotificationService notifier = new SMSNotificationService();
        InventoryService inventory = new WarehouseInventoryService();

        OrderService orderService = new OrderService(payment, notifier, inventory);
        orderService.checkout("MACBOOK-M3", 1, 199999.0);
    }
}
```

### Why it better demonstrates the concept:
- ✅ **100% Swappable Components:** Switch from Stripe to PayPal or SMS to WhatsApp by changing a single line in the Composition Root.
- ✅ **Frictionless Mocking for Tests:** Pass `MockPaymentGateway` in JUnit tests without invoking external APIs.
- ✅ **Full SOLID Adherence:** High-level modules depend purely on abstractions (DIP).

---

## Java Classes

- **`PaymentGateway`, `NotificationService`, `InventoryService` (Abstraction Interfaces):** Define domain service contracts.
- **`RazorpayPaymentGateway`, `StripePaymentGateway`, `EmailNotificationService`, `SMSNotificationService` (Concrete Implementations):** Service providers fulfilling contracts.
- **`OrderService` (Client Service):** Core business logic utilizing constructor-injected collaborators.
- **`MockPaymentGateway` (Test Double):** Demonstrates unit testing without third-party dependencies.
- **`DependencyInjectionExample` (Composition Root):** Assembles and wires the application dependency graph.

---

## How It Works

1. The **Composition Root** (`main()` or Spring IoC container) instantiates concrete implementations (`StripePaymentGateway`, `SMSNotificationService`).
2. The dependencies are passed into the `OrderService` constructor.
3. `OrderService` stores dependencies in `private final` fields.
4. When `orderService.checkout()` is called, it delegates execution to the abstractions without knowing the underlying concrete classes.

---

## When to Use

- **Complex Systems with External Integrations:** Payment processors, cloud storage providers (S3 vs GCS), database repositories.
- **Applications Requiring Comprehensive Unit Testing:** Isolating business workflows from external networks or file systems using mock objects.
- **Multi-Tenant or Config-Driven Architectures:** Dynamically injecting production vs sandbox vs staging services.

---

## When NOT to Use

- **Pure Static Utility Classes:** Math helpers, string manipulators (`StringUtils.capitalize()`) with no internal mutable state.
- **Data Transfer Objects (DTOs) & Value Objects:** Entities that merely encapsulate state without behavior (`UserDTO`, `Point(x, y)`).
- **Simple One-Off Scripts:** Adding DI boilerplate to a 20-line throwaway CLI script adds unnecessary overhead.

---

## LLD Takeaway

Dependency Injection is the fundamental architectural pillar connecting **SOLID Principles (DIP, OCP)** to real-world frameworks (Spring, Guice, Dagger). In Low-Level Design interviews, always declare dependencies as interfaces and inject them via constructors.

---

## 🎯 Quick Summary

- **Core Idea:** Pass dependencies into an object from the outside rather than letting the object instantiate them internally with `new`.
- **Code Demonstrates:** Constructor injection decoupling `OrderService` from `StripePaymentGateway`, `RazorpayPaymentGateway`, and `MockPaymentGateway`.
- **LLD Takeaway:** Use Constructor Injection with interfaces to achieve 100% testable, swappable, and loosely coupled systems.
- **Memorable Rule:** *"Depend on abstractions, inject through constructors, and instantiate only in the Composition Root."*
