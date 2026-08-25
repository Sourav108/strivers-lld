public interface TrafficLightState {
    void turnGreen(TrafficLight trafficLight);
    void turnYellow(TrafficLight trafficLight);
    void turnRed(TrafficLight trafficLight);
    void turnOff(TrafficLight trafficLight);
    String getStateName();
}
