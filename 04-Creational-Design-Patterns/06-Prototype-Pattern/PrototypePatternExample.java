import java.util.HashMap;
import java.util.Map;

/**
 * Creational Design Patterns: Prototype Pattern
 * 
 * Core Concept: Creates new objects by cloning pre-configured prototype instances
 * rather than constructing new ones from scratch through expensive initialization.
 */

// =========================================================================
// 1. PROTOTYPE INTERFACE
// =========================================================================

interface EmailTemplate extends Cloneable {
    EmailTemplate clone(); // Deep copy contract
    void setContent(String content);
    void send(String recipient);
}

// =========================================================================
// 2. CONCRETE PROTOTYPES
// =========================================================================

class WelcomeEmail implements EmailTemplate {
    private String subject;
    private String content;
    private String headerBrandLogo;
    private String footerLegalTerms;

    public WelcomeEmail() {
        // Simulating heavy resource-intensive initialization (e.g. loading templates, assets)
        this.subject = "Welcome to TUF Plus!";
        this.content = "Hi there! Welcome aboard.";
        this.headerBrandLogo = "https://cdn.takeuforward.org/assets/logo.png";
        this.footerLegalTerms = "© 2026 TakeUForward Inc. All rights reserved.";
    }

    // Copy Constructor for Deep Copy / Cloning
    public WelcomeEmail(WelcomeEmail source) {
        this.subject = source.subject;
        this.content = source.content;
        this.headerBrandLogo = source.headerBrandLogo;
        this.footerLegalTerms = source.footerLegalTerms;
    }

    @Override
    public WelcomeEmail clone() {
        return new WelcomeEmail(this);
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void send(String recipient) {
        System.out.println("📧 [Welcome Email -> " + recipient + "]");
        System.out.println("   Subject: " + subject);
        System.out.println("   Content: " + content);
        System.out.println("   Footer:  " + footerLegalTerms);
    }
}

class DiscountEmail implements EmailTemplate {
    private String subject;
    private String discountCode;
    private String content;

    public DiscountEmail() {
        this.subject = "Exclusive 50% Off on TUF+ Annual Subscription!";
        this.discountCode = "TUF50_SUPER";
        this.content = "Use code at checkout to claim your discount.";
    }

    public DiscountEmail(DiscountEmail source) {
        this.subject = source.subject;
        this.discountCode = source.discountCode;
        this.content = source.content;
    }

    @Override
    public DiscountEmail clone() {
        return new DiscountEmail(this);
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    @Override
    public void send(String recipient) {
        System.out.println("🎁 [Discount Email -> " + recipient + "]");
        System.out.println("   Subject: " + subject);
        System.out.println("   Code:    " + discountCode);
        System.out.println("   Content: " + content);
    }
}

// =========================================================================
// 3. PROTOTYPE REGISTRY (Prototype Cache)
// =========================================================================

class EmailTemplateRegistry {
    private static final Map<String, EmailTemplate> prototypes = new HashMap<>();

    static {
        // Pre-configure master prototypes during system startup
        prototypes.put("welcome", new WelcomeEmail());
        prototypes.put("discount", new DiscountEmail());
    }

    public static EmailTemplate getTemplate(String type) {
        EmailTemplate prototype = prototypes.get(type.toLowerCase());
        if (prototype == null) {
            throw new IllegalArgumentException("Unknown template type: " + type);
        }
        // Always return a fresh clone, never the original prototype!
        return prototype.clone();
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class PrototypePatternExample {
    public static void main(String[] args) {
        System.out.println("=== 1. Cloning Welcome Email Prototypes ===");
        
        // Clone 1 for Alice
        EmailTemplate aliceEmail = EmailTemplateRegistry.getTemplate("welcome");
        aliceEmail.setContent("Hi Alice! We're excited to have you in the LLD Masterclass.");
        aliceEmail.send("alice@example.com");

        System.out.println();

        // Clone 2 for Bob
        EmailTemplate bobEmail = EmailTemplateRegistry.getTemplate("welcome");
        bobEmail.setContent("Hi Bob! Your TUF Plus subscription is now active.");
        bobEmail.send("bob@example.com");

        System.out.println("\n=== 2. Cloning Discount Email Prototype ===");
        
        // Clone 3 for Charlie with custom coupon
        EmailTemplate charlieDiscount = EmailTemplateRegistry.getTemplate("discount");
        charlieDiscount.setContent("Hi Charlie, here is your personalized early-bird offer!");
        charlieDiscount.send("charlie@example.com");
    }
}
