import java.util.ArrayList;
import java.util.List;

/**
 * Behavioural Design Patterns: Mediator Pattern
 * 
 * Core Concept: Centralizes complex communication between multiple objects (Colleagues)
 * into a single mediation object, converting an O(N^2) mesh network into an O(N) star topology.
 */

// =========================================================================
// 1. MEDIATOR INTERFACE
// =========================================================================

interface DocumentSessionMediator {
    void join(User user);
    void broadcastChange(String change, User sender);
}

// =========================================================================
// 2. CONCRETE MEDIATOR (Hub coordinating live collaborative session)
// =========================================================================

class CollaborativeDocument implements DocumentSessionMediator {
    private final String documentTitle;
    private final List<User> collaborators = new ArrayList<>();

    public CollaborativeDocument(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    @Override
    public void join(User user) {
        collaborators.add(user);
        System.out.println("🟢 [Room: '" + documentTitle + "'] " + user.getName() + " joined the live editing session.");
    }

    @Override
    public void broadcastChange(String change, User sender) {
        System.out.println("📡 [Mediator Hub] Broadcasting edit from @" + sender.getName() + " to " + (collaborators.size() - 1) + " peers...");
        for (User collaborator : collaborators) {
            // Suppress echoing back to the sender
            if (collaborator != sender) {
                collaborator.receiveChange(change, sender);
            }
        }
    }
}

// =========================================================================
// 3. COLLEAGUE CLASS (Communicates exclusively via Mediator)
// =========================================================================

class User {
    protected final String name;
    protected final DocumentSessionMediator mediator;

    public User(String name, DocumentSessionMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    public void makeChange(String changeDescription) {
        System.out.println("\n✍️ [" + name + " (Editor)] Edited content: \"" + changeDescription + "\"");
        mediator.broadcastChange(changeDescription, this);
    }

    public void receiveChange(String changeDescription, User sender) {
        System.out.println("   📥 @" + name + " synced change from @" + sender.getName() + ": \"" + changeDescription + "\"");
    }

    public String getName() {
        return name;
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class MediatorPatternExample {
    public static void main(String[] args) {
        System.out.println("=== 📝 Google Docs Real-Time Collaboration (Mediator Pattern) ===\n");

        // 1. Instantiate the Central Mediator Hub
        DocumentSessionMediator docSession = new CollaborativeDocument("System_Design_Roadmap_2026.docx");

        // 2. Instantiate Colleagues with the Mediator reference
        User alice = new User("Alice", docSession);
        User bob = new User("Bob", docSession);
        User charlie = new User("Charlie", docSession);

        // 3. Users join the document session
        docSession.join(alice);
        docSession.join(bob);
        docSession.join(charlie);

        // 4. Alice makes an edit -> Mediator routes to Bob & Charlie
        alice.makeChange("Added Section 1: Low-Level Design Fundamentals");

        // 5. Bob makes an edit -> Mediator routes to Alice & Charlie
        bob.makeChange("Appended Diagram: Factory vs Abstract Factory Pattern");

        // 6. Charlie makes an edit -> Mediator routes to Alice & Bob
        charlie.makeChange("Fixed typo in Section 1 summary notes");
    }
}
