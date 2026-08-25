import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * YAGNI Principle: You Aren't Gonna Need It
 * 
 * Core Concept: Implement only what is required right now. Do not add speculative
 * features or abstractions for hypothetical future needs.
 */

// ==========================================
// ❌ BAD DESIGN (Violates YAGNI: Speculative over-engineering)
// Requirement: Simply create and view notes.
// Developer adds: Tags, Categories, Cloud Sync, Version History preemptively.
// ==========================================
class BadNote {
    private String id;
    private String content;
    private List<String> tags;            // ❌ Unrequested feature
    private String category;              // ❌ Unrequested feature
    private int version;                  // ❌ Unrequested feature
    private boolean isSyncedToCloud;      // ❌ Unrequested feature

    public BadNote(String id, String content) {
        this.id = id;
        this.content = content;
        this.tags = new ArrayList<>();
        this.version = 1;
        this.isSyncedToCloud = false;
    }
    // Plus 100 lines of unused sync, tagging, and versioning methods...
}

// ==========================================
// ✅ GOOD DESIGN (Adheres to YAGNI: Meets current requirements only)
// ==========================================
// Class 1: Simple Note entity holding only required fields.
class Note {
    private final String id;
    private final String content;

    public Note(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() { return id; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return "[" + id + "] " + content;
    }
}

// Class 2: NoteService managing only creation and retrieval of notes.
class NoteService {
    private final List<Note> notes = new ArrayList<>();

    public void addNote(String id, String content) {
        notes.add(new Note(id, content));
    }

    public List<Note> getNotes() {
        return Collections.unmodifiableList(notes);
    }
}

public class YAGNIExample {
    public static void main(String[] args) {
        NoteService service = new NoteService();
        service.addNote("N1", "Buy groceries");
        service.addNote("N2", "Prepare LLD notes");

        for (Note note : service.getNotes()) {
            System.out.println(note);
        }
    }
}
