package domain.state;

import domain.TrafficLight;

public class YellowState implements TrafficLightState {
    @Override
    public void turnGreen(TrafficLight trafficLight) {
        throw new InvalidStateTransitionException("❌ Invalid transition: Cannot change directly from YELLOW to GREEN. Must change to RED.");
    }

    @Override
    public void turnYellow(TrafficLight trafficLight) {
        // Already YELLOW, no-op
    }

    @Override
    public void turnRed(TrafficLight trafficLight) {
        trafficLight.setState(new RedState());
    }

    @Override
    public void turnOff(TrafficLight trafficLight) {
        trafficLight.setState(new OffState());
    }

    @Override
    public String getStateName() {
        return "YELLOW";
    }
}
