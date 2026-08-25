/**
 * SOLID Principles: Dependency Inversion Principle (DIP)
 * 
 * Core Concept:
 * 1. High-level modules should not depend on low-level modules. Both should depend on abstractions.
 * 2. Abstractions should not depend on details. Details should depend on abstractions.
 */

// =========================================================================
// ❌ BAD DESIGN (Violates DIP: Tightly Coupled Direct Dependency)
// RecommendationEngine (High-Level) directly instantiates RecentlyAdded (Low-Level).
// =========================================================================

class BadRecentlyAdded {
    public void getRecommendations() {
        System.out.println("[BadRecentlyAdded] Showing recently added shows: Inception, Interstellar");
    }
}

class BadRecommendationEngine {
    // ❌ Hardcoded tight coupling to concrete implementation
    private final BadRecentlyAdded recommender = new BadRecentlyAdded();

    public void recommend() {
        recommender.getRecommendations();
    }
}

// =========================================================================
// ✅ GOOD DESIGN (Adheres to DIP: Decoupled via Abstraction)
// =========================================================================

// Step 1: The Abstraction (Contract)
interface RecommendationStrategy {
    void getRecommendations();
}

// Step 2: Low-Level Modules implementing the Abstraction
class RecentlyAddedStrategy implements RecommendationStrategy {
    @Override
    public void getRecommendations() {
        System.out.println("🎬 [Recently Added] New this week: Inception, Oppenheimer, Interstellar");
    }
}

class TrendingNowStrategy implements RecommendationStrategy {
    @Override
    public void getRecommendations() {
        System.out.println("🔥 [Trending Now] Top 10 in India: Stranger Things, Breaking Bad, Dark");
    }
}

class GenreBasedStrategy implements RecommendationStrategy {
    private final String genre;

    public GenreBasedStrategy(String genre) {
        this.genre = genre;
    }

    @Override
    public void getRecommendations() {
        System.out.println("🍿 [Genre-Based (" + genre + ")] Recommendations: Blade Runner 2049, Matrix");
    }
}

// Step 3: High-Level Module depending purely on Abstraction
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

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class DIPExample {
    public static void main(String[] args) {
        System.out.println("=== ❌ 1. Bad Design: Tightly Coupled Recommendation Engine ===");
        BadRecommendationEngine badEngine = new BadRecommendationEngine();
        badEngine.recommend();

        System.out.println("\n=== ✅ 2. Good Design: Decoupled via Dependency Inversion ===");
        
        // Initializing engine with Trending Now strategy
        RecommendationStrategy trending = new TrendingNowStrategy();
        RecommendationEngine engine = new RecommendationEngine(trending);
        engine.recommend();

        // Switching dynamically to Genre-Based recommendations without modifying engine code
        System.out.println("\n--- Switching Strategy to Genre-Based at Runtime ---");
        engine.setStrategy(new GenreBasedStrategy("Sci-Fi"));
        engine.recommend();

        // Switching dynamically to Recently Added recommendations
        System.out.println("\n--- Switching Strategy to Recently Added at Runtime ---");
        engine.setStrategy(new RecentlyAddedStrategy());
        engine.recommend();
    }
}
