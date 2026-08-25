import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Best Practices in LLD: Database Design and Integration
 * Use-Case: Razorpay Payment Gateway
 * 
 * Demonstrates:
 * 1. ER-to-Class Domain Modeling (User, Merchant, Payment, Invoice)
 * 2. Relational Schema Normalization (3NF Entities with Primary & Foreign Keys)
 * 3. DAO Pattern (Data Access Object: Table-Centric CRUD)
 * 4. Repository Pattern (Domain Collection Abstraction)
 * 5. Separation of Concerns & Clean Architecture
 */

public class DatabaseDesignAndIntegrationExample {

    // =========================================================================
    // 1. DOMAIN MODELS (Mapped from ER Diagram)
    // =========================================================================

    enum PaymentStatus { PENDING, SUCCESS, FAILED, REFUNDED }
    enum PaymentMethod { UPI, CREDIT_CARD, DEBIT_CARD, NET_BANKING }

    // User Entity (Mapped from 'users' table)
    static class User {
        private final String userId;
        private final String name;
        private final String email;
        private final boolean kycVerified;

        public User(String userId, String name, String email, boolean kycVerified) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.kycVerified = kycVerified;
        }

        public String getUserId() { return userId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public boolean isKycVerified() { return kycVerified; }
    }

    // Merchant Entity (Mapped from 'merchants' table)
    static class Merchant {
        private final String merchantId;
        private final String businessName;
        private final String settlementAccount;

        public Merchant(String merchantId, String businessName, String settlementAccount) {
            this.merchantId = merchantId;
            this.businessName = businessName;
            this.settlementAccount = settlementAccount;
        }

        public String getMerchantId() { return merchantId; }
        public String getBusinessName() { return businessName; }
    }

    // Payment Entity (Aggregate Root mapped from 'payments' table)
    static class Payment {
        private final String paymentId;
        private final String userId;
        private final String merchantId;
        private final double amount;
        private final PaymentMethod method;
        private PaymentStatus status;
        private final String timestamp;

        public Payment(String paymentId, String userId, String merchantId, double amount, PaymentMethod method) {
            this.paymentId = paymentId;
            this.userId = userId;
            this.merchantId = merchantId;
            this.amount = amount;
            this.method = method;
            this.status = PaymentStatus.PENDING;
            this.timestamp = Instant.now().toString();
        }

        public void markSuccess() { this.status = PaymentStatus.SUCCESS; }
        public void markFailed() { this.status = PaymentStatus.FAILED; }

        public String getPaymentId() { return paymentId; }
        public String getUserId() { return userId; }
        public String getMerchantId() { return merchantId; }
        public double getAmount() { return amount; }
        public PaymentMethod getMethod() { return method; }
        public PaymentStatus getStatus() { return status; }
        public String getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("Payment[ID=%s, User=%s, Merchant=%s, Amount=₹%.2f, Method=%s, Status=%s]",
                    paymentId, userId, merchantId, amount, method, status);
        }
    }

    // Invoice Entity (Mapped from 'invoices' table)
    static class Invoice {
        private final String invoiceId;
        private final String paymentId;
        private final double gstAmount;
        private final double netTotal;
        private final String issuedAt;

        public Invoice(String invoiceId, String paymentId, double baseAmount) {
            this.invoiceId = invoiceId;
            this.paymentId = paymentId;
            this.gstAmount = baseAmount * 0.18; // 18% GST
            this.netTotal = baseAmount + this.gstAmount;
            this.issuedAt = Instant.now().toString();
        }

        public String getInvoiceId() { return invoiceId; }
        public double getGstAmount() { return gstAmount; }
        public double getNetTotal() { return netTotal; }
    }

    // =========================================================================
    // 2. DATA ACCESS OBJECT (DAO) LAYER - Table Centric CRUD
    // =========================================================================

    interface PaymentDAO {
        void insert(Payment payment);
        Payment getById(String paymentId);
        List<Payment> getByUserId(String userId);
        void updateStatus(String paymentId, PaymentStatus status);
    }

    // Concrete DAO implementing simulated low-level SQL database table access
    static class PaymentDAOImpl implements PaymentDAO {
        // Simulates the 'payments' SQL table
        private final Map<String, Payment> table = new ConcurrentHashMap<>();

        @Override
        public void insert(Payment payment) {
            System.out.println("      [SQL DAO Exec] INSERT INTO payments VALUES ('" + payment.getPaymentId() + "', " + payment.getAmount() + "...)");
            table.put(payment.getPaymentId(), payment);
        }

        @Override
        public Payment getById(String paymentId) {
            System.out.println("      [SQL DAO Exec] SELECT * FROM payments WHERE payment_id = '" + paymentId + "'");
            return table.get(paymentId);
        }

        @Override
        public List<Payment> getByUserId(String userId) {
            System.out.println("      [SQL DAO Exec] SELECT * FROM payments WHERE user_id = '" + userId + "'");
            return table.values().stream()
                    .filter(p -> p.getUserId().equals(userId))
                    .collect(Collectors.toList());
        }

        @Override
        public void updateStatus(String paymentId, PaymentStatus status) {
            System.out.println("      [SQL DAO Exec] UPDATE payments SET status = '" + status + "' WHERE payment_id = '" + paymentId + "'");
            Payment p = table.get(paymentId);
            if (p != null) p.markSuccess();
        }
    }

    // =========================================================================
    // 3. REPOSITORY LAYER - High-Level Domain Collection Abstraction
    // =========================================================================

    interface PaymentRepository {
        void save(Payment payment);
        Optional<Payment> findById(String paymentId);
        List<Payment> findSettledPaymentsByUser(String userId);
    }

    static class PaymentRepositoryImpl implements PaymentRepository {
        private final PaymentDAO paymentDAO;

        public PaymentRepositoryImpl(PaymentDAO paymentDAO) {
            this.paymentDAO = paymentDAO;
        }

        @Override
        public void save(Payment payment) {
            paymentDAO.insert(payment);
        }

        @Override
        public Optional<Payment> findById(String paymentId) {
            return Optional.ofNullable(paymentDAO.getById(paymentId));
        }

        @Override
        public List<Payment> findSettledPaymentsByUser(String userId) {
            return paymentDAO.getByUserId(userId).stream()
                    .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                    .collect(Collectors.toList());
        }
    }

    // =========================================================================
    // 4. HIGH-LEVEL DOMAIN SERVICE (Business Logic Orchestration)
    // =========================================================================

    static class RazorpayPaymentService {
        private final PaymentRepository paymentRepository;

        public RazorpayPaymentService(PaymentRepository paymentRepository) {
            this.paymentRepository = paymentRepository;
        }

        public Payment processPayment(User user, Merchant merchant, double amount, PaymentMethod method) {
            System.out.println("\n💳 [Razorpay Engine] Processing payment for " + user.getName() + " -> " + merchant.getBusinessName());

            // 1. KYC Validation
            if (!user.isKycVerified()) {
                throw new IllegalStateException("User KYC is incomplete. Cannot process payment.");
            }

            // 2. Create Domain Entity
            String paymentId = "PAY_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Payment payment = new Payment(paymentId, user.getUserId(), merchant.getMerchantId(), amount, method);

            // 3. Simulate Gateway Settlement & State Transition
            payment.markSuccess();

            // 4. Persist via Domain Repository
            paymentRepository.save(payment);

            // 5. Generate Tax Invoice
            Invoice invoice = new Invoice("INV_" + paymentId, paymentId, amount);
            System.out.println("   🧾 Generated Tax Invoice: " + invoice.getInvoiceId() + 
                               " (Base: ₹" + amount + " + GST (18%): ₹" + invoice.getGstAmount() + " = Total: ₹" + invoice.getNetTotal() + ")");

            return payment;
        }

        public List<Payment> getUserSettledHistory(String userId) {
            return paymentRepository.findSettledPaymentsByUser(userId);
        }
    }

    // =========================================================================
    // 🚀 MAIN DRIVER PROGRAM
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 🏦 Razorpay Payment Gateway: Database Design & Integration ===");

        // Setup Architecture: DAO -> Repository -> Service
        PaymentDAO paymentDAO = new PaymentDAOImpl();
        PaymentRepository paymentRepository = new PaymentRepositoryImpl(paymentDAO);
        RazorpayPaymentService paymentService = new RazorpayPaymentService(paymentRepository);

        // Seed Entities
        User customer1 = new User("USR-101", "Sourav Saha", "sourav@takeuforward.org", true);
        User customer2 = new User("USR-102", "Raj Vikramaditya", "striver@takeuforward.org", true);
        Merchant merchant = new Merchant("MERCH-999", "TUF EdTech Pvt Ltd", "HDFC0001234");

        // --- Demo 1: Process UPI Transaction ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ DEMO 1: Process Transaction & Persist to Database");
        System.out.println("-----------------------------------------------------------");
        Payment txn1 = paymentService.processPayment(customer1, merchant, 2999.0, PaymentMethod.UPI);
        System.out.println("✅ Transaction Complete: " + txn1);

        // --- Demo 2: Process Credit Card Transaction ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ DEMO 2: Process Second Transaction for Same User");
        System.out.println("-----------------------------------------------------------");
        Payment txn2 = paymentService.processPayment(customer1, merchant, 1499.0, PaymentMethod.CREDIT_CARD);
        System.out.println("✅ Transaction Complete: " + txn2);

        // --- Demo 3: Query Domain Repository for User History ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ DEMO 3: Query User Settled Payments via Repository");
        System.out.println("-----------------------------------------------------------");
        List<Payment> history = paymentService.getUserSettledHistory(customer1.getUserId());
        System.out.println("📜 Settled Payment Ledger for " + customer1.getName() + " (" + history.size() + " records found):");
        for (Payment p : history) {
            System.out.println("   - " + p);
        }

        System.out.println("\n===========================================================");
        System.out.println("🎯 ER Mapping, DAO, and Repository Integration Verified!");
        System.out.println("===========================================================");
    }
}
