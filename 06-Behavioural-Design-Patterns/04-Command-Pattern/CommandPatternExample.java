import java.util.Stack;

/**
 * Behavioural Design Patterns: Command Pattern
 * 
 * Core Concept: Encapsulates a request as an object, decoupling the sender
 * (Invoker) from the receiver (Receiver), enabling undo/redo and command queuing.
 */

// =========================================================================
// 1. RECEIVERS (Hardware / Business components with domain logic)
// =========================================================================

class Light {
    private final String location;

    public Light(String location) {
        this.location = location;
    }

    public void on() {
        System.out.println("💡 [" + location + " Light] Turned ON (Brightness: 100%)");
    }

    public void off() {
        System.out.println("💡 [" + location + " Light] Turned OFF");
    }
}

class AirConditioner {
    private final String room;
    private int temperature = 24;

    public AirConditioner(String room) {
        this.room = room;
    }

    public void on() {
        System.out.println("❄️ [" + room + " AC] Power ON (Set to " + temperature + "°C)");
    }

    public void off() {
        System.out.println("❄️ [" + room + " AC] Power OFF");
    }
}

// =========================================================================
// 2. COMMAND INTERFACE
// =========================================================================

interface Command {
    void execute();
    void undo();
}

// =========================================================================
// 3. CONCRETE COMMANDS (Binding Receivers to Actions)
// =========================================================================

class LightOnCommand implements Command {
    private final Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();
    }

    @Override
    public void undo() {
        light.off();
    }
}

class LightOffCommand implements Command {
    private final Light light;

    public LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.off();
    }

    @Override
    public void undo() {
        light.on();
    }
}

class ACOnCommand implements Command {
    private final AirConditioner ac;

    public ACOnCommand(AirConditioner ac) {
        this.ac = ac;
    }

    @Override
    public void execute() {
        ac.on();
    }

    @Override
    public void undo() {
        ac.off();
    }
}

class ACOffCommand implements Command {
    private final AirConditioner ac;

    public ACOffCommand(AirConditioner ac) {
        this.ac = ac;
    }

    @Override
    public void execute() {
        ac.off();
    }

    @Override
    public void undo() {
        ac.on();
    }
}

// =========================================================================
// 4. INVOKER (Remote Control with Multi-Level Undo History Stack)
// =========================================================================

class RemoteControl {
    private final Command[] slots = new Command[4];
    private final Stack<Command> history = new Stack<>();

    public void setCommand(int slot, Command command) {
        if (slot >= 0 && slot < slots.length) {
            slots[slot] = command;
        }
    }

    public void pressButton(int slot) {
        if (slot >= 0 && slot < slots.length && slots[slot] != null) {
            System.out.print("👉 [Button " + slot + " Pressed] ");
            slots[slot].execute();
            history.push(slots[slot]); // Record in undo stack
        } else {
            System.out.println("⚠️ No command configured for slot " + slot);
        }
    }

    public void pressUndo() {
        if (!history.isEmpty()) {
            Command lastCommand = history.pop();
            System.out.print("↩️ [Undo Triggered] ");
            lastCommand.undo();
        } else {
            System.out.println("⚠️ History is empty. No commands to undo.");
        }
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class CommandPatternExample {
    public static void main(String[] args) {
        System.out.println("=== 🎛️ Smart Home Automation with Command Pattern ===");

        // 1. Initialize Receivers
        Light livingRoomLight = new Light("Living Room");
        AirConditioner bedroomAC = new AirConditioner("Master Bedroom");

        // 2. Wrap Actions into Command Objects
        Command lightOn = new LightOnCommand(livingRoomLight);
        Command lightOff = new LightOffCommand(livingRoomLight);
        Command acOn = new ACOnCommand(bedroomAC);
        Command acOff = new ACOffCommand(bedroomAC);

        // 3. Configure Invoker Slots
        RemoteControl remote = new RemoteControl();
        remote.setCommand(0, lightOn);
        remote.setCommand(1, lightOff);
        remote.setCommand(2, acOn);
        remote.setCommand(3, acOff);

        // 4. Execute Actions via Invoker
        System.out.println("\n--- Step 1: Performing Actions ---");
        remote.pressButton(0); // Turn Light ON
        remote.pressButton(2); // Turn AC ON
        remote.pressButton(1); // Turn Light OFF

        // 5. Multi-Level Undo Operations
        System.out.println("\n--- Step 2: Step-by-Step Reversible Undo ---");
        remote.pressUndo(); // Undoes Light OFF -> Light turns ON
        remote.pressUndo(); // Undoes AC ON -> AC turns OFF
        remote.pressUndo(); // Undoes Light ON -> Light turns OFF
        remote.pressUndo(); // Stack empty
    }
}
