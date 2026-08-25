import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Structural Design Patterns: Flyweight Pattern
 * 
 * Core Concept: Minimizes memory usage by sharing immutable intrinsic state
 * across massive numbers of similar objects, keeping extrinsic state external.
 */

// =========================================================================
// 1. FLYWEIGHT CLASS (Intrinsic State: Shared & Immutable)
// =========================================================================

class TreeType {
    // Intrinsic attributes (identical for all trees of this type)
    private final String name;
    private final String color;
    private final String texture;

    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
    }

    // Extrinsic state (x, y) is passed in as method parameters
    public void draw(int x, int y) {
        System.out.println("🌲 [TreeType: " + name + "] Rendering (" + color + ", " + texture + ") at coordinates (" + x + ", " + y + ")");
    }

    public String getName() { return name; }
}

// =========================================================================
// 2. FLYWEIGHT FACTORY (Object Cache / Pool)
// =========================================================================

class TreeFactory {
    private static final Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String texture) {
        String key = name + "_" + color + "_" + texture;
        if (!treeTypes.containsKey(key)) {
            System.out.println("✨ [TreeFactory] Creating NEW shared TreeType instance for key: " + key);
            treeTypes.put(key, new TreeType(name, color, texture));
        }
        return treeTypes.get(key);
    }

    public static int getFlyweightCount() {
        return treeTypes.size();
    }
}

// =========================================================================
// 3. CONTEXT OBJECT (Extrinsic State + Reference to Flyweight)
// =========================================================================

class Tree {
    // Extrinsic state (unique per individual tree)
    private final int x;
    private final int y;

    // Reference to shared Flyweight object
    private final TreeType type;

    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw() {
        type.draw(x, y);
    }
}

// =========================================================================
// 4. CLIENT CONTAINER (Forest managing trees)
// =========================================================================

class Forest {
    private final List<Tree> trees = new ArrayList<>();

    public void plantTree(int x, int y, String name, String color, String texture) {
        TreeType type = TreeFactory.getTreeType(name, color, texture);
        Tree tree = new Tree(x, y, type);
        trees.add(tree);
    }

    public void drawForest(int maxDisplay) {
        int count = 0;
        for (Tree tree : trees) {
            if (count++ < maxDisplay) {
                tree.draw();
            }
        }
        if (trees.size() > maxDisplay) {
            System.out.println("... and " + (trees.size() - maxDisplay) + " more trees rendered seamlessly!");
        }
    }

    public int getTotalTreeCount() {
        return trees.size();
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class FlyweightPatternExample {
    public static void main(String[] args) {
        System.out.println("=== 🗺️ Rendering 100,000 Trees in Forest with Flyweight Pattern ===");

        Forest forest = new Forest();

        // Planting trees across 3 different species
        System.out.println("\n--- Initializing Trees ---");
        for (int i = 0; i < 40000; i++) {
            forest.plantTree(i, i * 2, "Oak", "Emerald Green", "Rough Bark Texture");
        }
        for (int i = 0; i < 35000; i++) {
            forest.plantTree(i + 100, i * 3, "Pine", "Dark Green", "Needle Texture");
        }
        for (int i = 0; i < 25000; i++) {
            forest.plantTree(i + 200, i * 4, "Birch", "Silver White", "Smooth Bark Texture");
        }

        System.out.println("\n--- Sample Rendered Trees ---");
        forest.drawForest(5);

        System.out.println("\n=======================================================");
        System.out.println("📊 Total Tree Context Objects:  " + forest.getTotalTreeCount());
        System.out.println("🧠 Total Shared Flyweight Types: " + TreeFactory.getFlyweightCount() + " (Oak, Pine, Birch)");
        System.out.println("💡 Result: 100,000 trees sharing just 3 Flyweight objects in memory!");
        System.out.println("=======================================================");
    }
}
