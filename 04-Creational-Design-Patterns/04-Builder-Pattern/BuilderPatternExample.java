import java.util.Arrays;
import java.util.List;

/**
 * Creational Design Patterns: Builder Pattern
 * 
 * Core Concept: Separates the step-by-step construction of a complex object
 * from its representation, allowing the same construction process to create
 * various representations while preserving immutability.
 */

// =========================================================================
// ❌ BAD DESIGN: Telescoping Constructor Anti-Pattern
// =========================================================================
class BadBurgerMeal {
    private String bunType;
    private String patty;
    private boolean hasCheese;
    private List<String> toppings;
    private String side;
    private String drink;

    // Constructor with mandatory + optional fields
    public BadBurgerMeal(String bunType, String patty, boolean hasCheese, List<String> toppings, String side, String drink) {
        this.bunType = bunType;
        this.patty = patty;
        this.hasCheese = hasCheese;
        this.toppings = toppings;
        this.side = side;
        this.drink = drink;
    }
}

// =========================================================================
// ✅ GOOD DESIGN: Builder Pattern with Fluent Method Chaining
// =========================================================================
class BurgerMeal {
    // Mandatory fields
    private final String bunType;
    private final String patty;

    // Optional fields
    private final boolean hasCheese;
    private final List<String> toppings;
    private final String side;
    private final String drink;

    // Private constructor: Enforces creation strictly via the Builder
    private BurgerMeal(BurgerBuilder builder) {
        this.bunType = builder.bunType;
        this.patty = builder.patty;
        this.hasCheese = builder.hasCheese;
        this.toppings = builder.toppings;
        this.side = builder.side;
        this.drink = builder.drink;
    }

    @Override
    public String toString() {
        return "🍔 BurgerMeal {" +
                "bun='" + bunType + '\'' +
                ", patty='" + patty + '\'' +
                ", cheese=" + hasCheese +
                ", toppings=" + (toppings != null ? toppings : "None") +
                ", side='" + (side != null ? side : "None") + '\'' +
                ", drink='" + (drink != null ? drink : "None") + '\'' +
                '}';
    }

    // Static Nested Builder Class
    public static class BurgerBuilder {
        // Required parameters
        private final String bunType;
        private final String patty;

        // Optional parameters (initialized to defaults)
        private boolean hasCheese = false;
        private List<String> toppings = null;
        private String side = null;
        private String drink = null;

        // Builder constructor with mandatory attributes
        public BurgerBuilder(String bunType, String patty) {
            this.bunType = bunType;
            this.patty = patty;
        }

        // Fluent chaining methods
        public BurgerBuilder withCheese(boolean hasCheese) {
            this.hasCheese = hasCheese;
            return this;
        }

        public BurgerBuilder withToppings(List<String> toppings) {
            this.toppings = toppings;
            return this;
        }

        public BurgerBuilder withSide(String side) {
            this.side = side;
            return this;
        }

        public BurgerBuilder withDrink(String drink) {
            this.drink = drink;
            return this;
        }

        // Terminal build method
        public BurgerMeal build() {
            // Optional step-by-step validation can be performed here
            return new BurgerMeal(this);
        }
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class BuilderPatternExample {
    public static void main(String[] args) {
        System.out.println("=== ❌ 1. Bad Design: Passing Confusing Nulls into Telescoping Constructor ===");
        // Hard to read, positional errors likely
        BadBurgerMeal badMeal = new BadBurgerMeal("Wheat", "Veggie", false, null, null, null);
        System.out.println("Created BadBurgerMeal with mandatory fields + 4 confusing nulls.\n");

        System.out.println("=== ✅ 2. Good Design: Fluent Builder Pattern ===");

        // 1. Plain Burger with only mandatory fields
        BurgerMeal plainBurger = new BurgerMeal.BurgerBuilder("Wheat Bun", "Veggie Patty")
                .build();
        System.out.println("1. Plain Burger: " + plainBurger);

        // 2. Custom Burger with Cheese only
        BurgerMeal cheeseBurger = new BurgerMeal.BurgerBuilder("Brioche Bun", "Crispy Chicken")
                .withCheese(true)
                .build();
        System.out.println("2. Cheese Burger: " + cheeseBurger);

        // 3. Fully Loaded Gourmet Meal
        BurgerMeal gourmetMeal = new BurgerMeal.BurgerBuilder("Sesame Brioche", "Smoked Beef")
                .withCheese(true)
                .withToppings(Arrays.asList("Caramelized Onions", "Pickles", "Jalapenos", "BBQ Sauce"))
                .withSide("Peri-Peri Waffle Fries")
                .withDrink("Chilled Dr Pepper")
                .build();
        System.out.println("3. Gourmet Meal: " + gourmetMeal);
    }
}
