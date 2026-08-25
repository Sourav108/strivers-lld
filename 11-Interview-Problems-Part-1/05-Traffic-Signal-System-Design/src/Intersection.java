import java.util.EnumMap;
import java.util.Map;

public class Intersection {
    private final int id;
    private final String name;
    private final Map<Direction, TrafficLight> trafficLights = new EnumMap<>(Direction.class);
    private final Map<Direction, SignalTiming> signalTimings = new EnumMap<>(Direction.class);
    private final Map<Direction, VehicleCounter> vehicleCounters = new EnumMap<>(Direction.class);
    private final IntersectionCycle cycle = new IntersectionCycle();
    private boolean isEmergencyMode = false;
    private Direction emergencyDirection = null;

    public Intersection(int id, String name) {
        this.id = id;
        this.name = name;

        // Initialize 4 traffic lights (all default RED), timings (default 10s GREEN), and vehicle counters
        for (Direction direction : Direction.values()) {
            trafficLights.put(direction, new TrafficLight(direction));
            signalTimings.put(direction, new SignalTiming(direction, 10, true));
            vehicleCounters.put(direction, new VehicleCounter(direction));
        }
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public IntersectionCycle getCycle() { return cycle; }
    public boolean isEmergencyMode() { return isEmergencyMode; }
    public Direction getEmergencyDirection() { return emergencyDirection; }

    public TrafficLight getTrafficLight(Direction direction) {
        return trafficLights.get(direction);
    }

    public SignalTiming getSignalTiming(Direction direction) {
        return signalTimings.get(direction);
    }

    public VehicleCounter getVehicleCounter(Direction direction) {
        return vehicleCounters.get(direction);
    }

    public void setEmergencyMode(boolean emergencyMode, Direction direction) {
        this.isEmergencyMode = emergencyMode;
        this.emergencyDirection = direction;
    }

    // Emergency transition: Safely bring all non-emergency signals to RED
    public void setAllSignalsToRed() {
        for (TrafficLight light : trafficLights.values()) {
            light.emergencyTransitionToRed();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Intersection '").append(name).append("' (ID: ").append(id).append(")\n");
        for (TrafficLight light : trafficLights.values()) {
            sb.append("   ").append(light).append(" ");
        }
        return sb.toString();
    }
}
