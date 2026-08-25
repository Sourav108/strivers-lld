import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Behavioural Design Patterns: Memento Pattern
 * 
 * Core Concept: Captures and externalizes an object's internal state into an
 * immutable snapshot (Memento) and restores it later without violating encapsulation.
 */

// =========================================================================
// 1. ORIGINATOR CLASS (Owns mutable state, produces & consumes Mementos)
// =========================================================================

class ResumeEditor {
    private String name;
    private String education;
    private String experience;
    private List<String> skills;

    public ResumeEditor() {
        this.skills = new ArrayList<>();
    }

    public void setDetails(String name, String education, String experience, List<String> skills) {
        this.name = name;
        this.education = education;
        this.experience = experience;
        this.skills = new ArrayList<>(skills); // Safe mutable copy
    }

    public void addSkill(String newSkill) {
        this.skills.add(newSkill);
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void printResume() {
        System.out.println("📄 ================== RESUME ==================");
        System.out.println("👤 Name:       " + name);
        System.out.println("🎓 Education:  " + education);
        System.out.println("💼 Experience: " + experience);
        System.out.println("🛠️ Skills:     " + skills);
        System.out.println("===============================================");
    }

    // Creates an immutable snapshot (Memento)
    public Memento save() {
        return new Memento(name, education, experience, List.copyOf(skills));
    }

    // Restores internal state from a Memento
    public void restore(Memento memento) {
        this.name = memento.getName();
        this.education = memento.getEducation();
        this.experience = memento.getExperience();
        this.skills = new ArrayList<>(memento.getSkills());
    }

    // =========================================================================
    // 2. MEMENTO (Immutable Inner Class holding State Snapshot)
    // =========================================================================
    public static class Memento {
        private final String name;
        private final String education;
        private final String experience;
        private final List<String> skills;

        private Memento(String name, String education, String experience, List<String> skills) {
            this.name = name;
            this.education = education;
            this.experience = experience;
            this.skills = skills;
        }

        private String getName() { return name; }
        private String getEducation() { return education; }
        private String getExperience() { return experience; }
        private List<String> getSkills() { return skills; }
    }
}

// =========================================================================
// 3. CARETAKER CLASS (Manages the History Stack of Mementos)
// =========================================================================

class ResumeHistory {
    private final Stack<ResumeEditor.Memento> history = new Stack<>();

    public void save(ResumeEditor editor) {
        history.push(editor.save());
        System.out.println("💾 [Caretaker] Saved resume checkpoint. (Total snapshots: " + history.size() + ")");
    }

    public void undo(ResumeEditor editor) {
        if (!history.isEmpty()) {
            ResumeEditor.Memento previousState = history.pop();
            editor.restore(previousState);
            System.out.println("↩️ [Caretaker] Undo performed. Restored to previous checkpoint.");
        } else {
            System.out.println("⚠️ [Caretaker] History stack is empty. No checkpoints to undo.");
        }
    }

    public int getHistorySize() {
        return history.size();
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class MementoPatternExample {
    public static void main(String[] args) {
        System.out.println("=== 📝 Resume Builder with Memento Pattern (Undo Support) ===\n");

        ResumeEditor editor = new ResumeEditor();
        ResumeHistory history = new ResumeHistory();

        // Stage 1: Initial Resume Creation (Fresher)
        editor.setDetails("Sourav Saha", "B.Tech Computer Science", "Fresher / College Graduate", Arrays.asList("Java", "Data Structures", "Algorithms"));
        history.save(editor); // Snapshot 1

        // Stage 2: Promotion / Internship
        System.out.println("\n--- Step 1: Updating to SDE Intern ---");
        editor.setExperience("SDE Intern at takeUforward");
        editor.addSkill("Spring Boot");
        editor.addSkill("Low-Level Design");
        history.save(editor); // Snapshot 2

        // Stage 3: Senior Role
        System.out.println("\n--- Step 2: Updating to Full-time SDE-2 ---");
        editor.setExperience("SDE-2 at Tier-1 Tech");
        editor.addSkill("Microservices");
        editor.addSkill("System Architecture");

        System.out.println("\nCurrent Live Resume:");
        editor.printResume();

        // Step 4: Undo Operations
        System.out.println("\n--- Step 3: Triggering First Undo (Revert to SDE Intern) ---");
        history.undo(editor);
        editor.printResume();

        System.out.println("\n--- Step 4: Triggering Second Undo (Revert to Fresher) ---");
        history.undo(editor);
        editor.printResume();

        System.out.println("\n--- Step 5: Triggering Extra Undo on Empty Stack ---");
        history.undo(editor);
    }
}
