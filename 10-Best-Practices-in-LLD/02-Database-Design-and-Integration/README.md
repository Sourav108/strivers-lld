# 02 - Database Design and Integration in LLD

## Core Idea

**Database Design and Integration** in Low-Level Design bridges the gap between high-level business domain requirements and physical data persistence. Using the **Razorpay Payment System** as an enterprise use case, this discipline encompasses **Entity-Relationship (ER) Modeling**, translating relational tables to Object-Oriented Class Models, and decoupling persistence operations from business logic using the **DAO (Data Access Object)** and **Repository** patterns.

---

## 💡 Real-Life Analogy (Razorpay Payment Gateway)

```
[User / Customer] ----(initiates)----> [Payment Transaction] ----(settles to)----> [Merchant Account]
       |                                      |                                          |
   (has KYC)                          (uses Method: UPI/Card)                     (generates Invoice)
```

- **Entities:** `User`, `Merchant`, `Payment`, `PaymentMethod`, `Invoice`.
- **Relational Tables:** Normalized SQL tables with Primary Keys ($PK$) and Foreign Keys ($FK$).
- **Application Class Model:** Domain entities with encapsulated business behaviors.
- **Persistence Layer:** `PaymentDAO` (direct SQL execution) and `PaymentRepository` (domain-centric collection).

---

## 🏛️ ER Model to Relational Schema Translation

```
+---------------------------------------------------------------------------------------+
| RELATIONAL DATABASE SCHEMA (3NF Normalized)                                           |
|                                                                                       |
| users(user_id PK, name, email, phone, kyc_status, created_at)                         |
| merchants(merchant_id PK, business_name, settlement_bank_acc, status)                 |
| payment_methods(method_id PK, user_id FK, type [CARD/UPI/NETBANKING], provider)       |
| payments(payment_id PK, user_id FK, merchant_id FK, amount, currency, status, tx_time)|
| invoices(invoice_id PK, payment_id FK, gst_amount, total_amount, issued_at)           |
+---------------------------------------------------------------------------------------+
```

---

## ⚖️ DAO vs. Repository Pattern

| Dimension | DAO (Data Access Object) | Repository Pattern |
|---|---|---|
| **Architectural Focus** | Low-level Database & Table-Centric. | High-level Domain Aggregate & Collection-Centric. |
| **Abstraction Level** | Close to the storage mechanism (SQL queries, JDBC). | Closer to domain business logic (in-memory collection feel). |
| **Granularity** | 1 DAO per database table (`PaymentDAO`, `UserDAO`). | 1 Repository per **Domain Aggregate Root** (`PaymentRepository`). |
| **Typical Methods** | `insert(row)`, `update(row)`, `deleteById(id)`. | `findSettledPaymentsForUser()`, `getMonthlyRevenue()`. |

---

## ❌ Bad Design (Direct Database Coupling in Business Logic)

```java
class BadPaymentService {
    public void processPayment(String userId, String merchantId, double amount) {
        // ❌ Leaking raw SQL queries and JDBC connections directly in the controller/service!
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db")) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO payments (user_id, merchant_id, amount, status) VALUES (?, ?, ?, 'SUCCESS')"
            );
            ps.setString(1, userId);
            ps.setString(2, merchantId);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### What is wrong?
- ⚠️ **Zero Testability:** Cannot unit test business workflows without running an active MySQL instance.
- ⚠️ **Tight SQL Coupling:** Changing database column names or switching from MySQL to PostgreSQL requires modifying business services.
- ⚠️ **Violates Single Responsibility Principle (SRP):** Business logic is tangled with raw connection pooling and SQL execution.

---

## ✅ Good Design (Clean Domain Model + Repository & DAO Separation)

```java
// 1. Domain Entities
public class Payment {
    private final String paymentId;
    private final String userId;
    private final String merchantId;
    private final double amount;
    private PaymentStatus status;

    public Payment(String paymentId, String userId, String merchantId, double amount) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public void markSuccess() { this.status = PaymentStatus.SUCCESS; }
    public String getPaymentId() { return paymentId; }
    public String getUserId() { return userId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
}

// 2. Repository Contract (Domain Collection Abstraction)
public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(String paymentId);
    List<Payment> findByUserId(String userId);
}

// 3. High-Level Service
public class RazorpayPaymentService {
    private final PaymentRepository paymentRepository;

    public RazorpayPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(String userId, String merchantId, double amount) {
        Payment payment = new Payment(UUID.randomUUID().toString(), userId, merchantId, amount);
        payment.markSuccess();
        paymentRepository.save(payment);
        return payment;
    }
}
```

### Why it better demonstrates the concept:
- ✅ **Decoupled Architecture:** Business logic interacts solely with domain abstractions (`PaymentRepository`), oblivious to whether data sits in MySQL, PostgreSQL, or an in-memory cache.
- ✅ **Effortless Mocking:** Pass an in-memory repository during unit tests without spin-up delays.
- ✅ **Domain-Driven Design (DDD):** Entities encapsulate status transitions (`markSuccess()`) preventing invalid mutations.

---

## Java Classes

- **`User` & `Merchant` (Domain Entities):** Represent core participants in the payment lifecycle.
- **`Payment` (Domain Aggregate Root):** Encapsulates monetary transactions and state transitions (`PENDING`, `SUCCESS`, `REFUNDED`).
- **`PaymentDAO` (Data Access Object):** Low-level CRUD interface simulating storage operations.
- **`PaymentRepository` (Domain Repository):** High-level domain collection interface.
- **`InMemoryPaymentRepository` & `PaymentDAOImpl`:** Concrete storage implementations.
- **`RazorpayPaymentService` (Domain Service):** Orchestrates payment processing and persistence.
- **`DatabaseDesignAndIntegrationExample` (Main Driver):** Tests and validates the complete payment persistence flow.

---

## How It Works

1. Client triggers `RazorpayPaymentService.createPayment()`.
2. The service creates an immutable domain `Payment` aggregate and verifies invariants.
3. The service calls `paymentRepository.save(payment)`.
4. The repository delegates to low-level storage (or `PaymentDAO`), storing the normalized entity.
5. Inquiries (`findByUserId()`) return strongly typed domain objects rather than raw JDBC result sets.

---

## When to Use

- **Enterprise Payment & Fintech Systems:** Razorpay, Stripe, PayPal transactional ledgers.
- **E-Commerce Marketplaces:** Amazon order processing, inventory reservations, customer invoicing.
- **High-Scale Web Backends:** Multi-tenant SaaS architectures separating persistence from core logic.

---

## When NOT to Use

- **Ultra-Simple CRUD Prototypes:** For a 100-line script, writing full DAOs and Repositories adds unnecessary architectural boilerplate.

---

## LLD Takeaway

Database integration and the **Repository / DAO Pattern** are critical for designing **Payment Gateways (Razorpay)**, **Hotel Booking Engines (Airbnb)**, and **Ride Sharing Systems (Uber)** in Low-Level Design interviews.

---

## 🎯 Quick Summary

- **Core Idea:** Translate real-world domain requirements into 3NF normalized tables and decouple storage via DAO and Repository patterns.
- **Code Demonstrates:** Razorpay payment lifecycle modeling with Domain Entities, `PaymentRepository` collection abstractions, and `PaymentDAO` persistence.
- **LLD Takeaway:** Never write raw SQL inside business services; use Repositories for domain aggregates and DAOs for table-level CRUD.
- **Memorable Rule:** *"Model ER diagrams into normalized tables; map tables to domain entities, and abstract storage behind repositories."*
