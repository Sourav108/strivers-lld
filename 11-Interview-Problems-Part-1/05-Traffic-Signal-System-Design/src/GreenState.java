public class GreenState implements TrafficLightState {
    @Override
    public void turnGreen(TrafficLight trafficLight) {
        // Already GREEN, no-op
    }

    @Override
    public void turnYellow(TrafficLight trafficLight) {
        trafficLight.setState(new YellowState());
    }

    @Override
    public void turnRed(TrafficLight trafficLight) {
        throw new InvalidStateTransitionException("❌ Invalid transition: Cannot change directly from GREEN to RED. Must pass through YELLOW.");
    }

    @Override
    public void turnOff(TrafficLight trafficLight) {
        trafficLight.setState(new OffState());
    }

    @Override
    public String getStateName() {
        return "GREEN";
    }
}
