package domain.state;

import domain.TrafficLight;

public class OffState implements TrafficLightState {
    @Override
    public void turnGreen(TrafficLight trafficLight) {
        trafficLight.setState(new GreenState());
    }

    @Override
    public void turnYellow(TrafficLight trafficLight) {
        trafficLight.setState(new YellowState());
    }

    @Override
    public void turnRed(TrafficLight trafficLight) {
        trafficLight.setState(new RedState());
    }

    @Override
    public void turnOff(TrafficLight trafficLight) {
        // Already OFF, no-op
    }

    @Override
    public String getStateName() {
        return "OFF";
    }
}
