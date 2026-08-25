/**
 * Structural Design Patterns: Decorator Pattern
 * 
 * Core Concept: Attaches additional responsibilities and behavior to an object
 * dynamically at runtime using wrapper objects that share the same interface.
 * 
 * Solves the "Class Explosion" problem of multi-level inheritance combinations.
 */

// =========================================================================
// 1. COMPONENT INTERFACE
// =========================================================================

interface Pizza {
    String getDescription();
    double getCost();
}

// =========================================================================
// 2. CONCRETE BASE COMPONENTS (Base pizzas without extra add-ons)
// =========================================================================

class PlainPizza implements Pizza {
    @Override
    public String getDescription() {
        return "Plain Thin-Crust Pizza";
    }

    @Override
    public double getCost() {
        return 150.00;
    }
}

class MargheritaPizza implements Pizza {
    @Override
    public String getDescription() {
        return "Classic Margherita Pizza";
    }

    @Override
    public double getCost() {
        return 200.00;
    }
}

// =========================================================================
// 3. ABSTRACT DECORATOR (Implements Pizza & Wraps another Pizza)
// =========================================================================

abstract class PizzaDecorator implements Pizza {
    protected final Pizza pizza;

    public PizzaDecorator(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription();
    }

    @Override
    public double getCost() {
        return pizza.getCost();
    }
}

// =========================================================================
// 4. CONCRETE DECORATORS (Dynamic Add-ons / Toppings)
// =========================================================================

class ExtraCheese extends PizzaDecorator {
    public ExtraCheese(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Extra Mozzarella Cheese";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 40.00;
    }
}

class Olives extends PizzaDecorator {
    public Olives(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Black Olives";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 30.00;
    }
}

class StuffedCrust extends PizzaDecorator {
    public StuffedCrust(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Cheese-Stuffed Crust";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 50.00;
    }
}

class Jalapenos extends PizzaDecorator {
    public Jalapenos(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Spicy Jalapenos";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 25.00;
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class DecoratorPatternExample {
    public static void main(String[] args) {
        System.out.println("=== 1. Basic Pizza without Toppings ===");
        Pizza basePizza = new PlainPizza();
        System.out.println("Item:  " + basePizza.getDescription());
        System.out.println("Total: ₹" + basePizza.getCost());

        System.out.println("\n=== 2. Margherita with Extra Cheese and Olives ===");
        // Dynamic Layering: Olives(ExtraCheese(MargheritaPizza))
        Pizza cheeseOlivePizza = new MargheritaPizza();
        cheeseOlivePizza = new ExtraCheese(cheeseOlivePizza);
        cheeseOlivePizza = new Olives(cheeseOlivePizza);
        System.out.println("Item:  " + cheeseOlivePizza.getDescription());
        System.out.println("Total: ₹" + cheeseOlivePizza.getCost());

        System.out.println("\n=== 3. Fully Loaded Gourmet Pizza (Layered in One-Liner) ===");
        // All toppings composed dynamically at runtime
        Pizza gourmetPizza = new StuffedCrust(
                new Jalapenos(
                        new Olives(
                                new ExtraCheese(
                                        new MargheritaPizza()
                                )
                        )
                )
        );
        System.out.println("Item:  " + gourmetPizza.getDescription());
        System.out.println("Total: ₹" + gourmetPizza.getCost());
    }
}
