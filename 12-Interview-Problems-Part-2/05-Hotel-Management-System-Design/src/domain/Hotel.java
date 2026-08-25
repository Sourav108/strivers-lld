package domain;

public class Hotel {
    private final String id;
    private final String name;
    private final String address;
    private final String city;
    private final String country;
    private final double latitude;
    private final double longitude;
    private double rating;
    private boolean isActive;
    private int defaultOverbookPercent; // e.g. 10%
    private String cancellationPolicyId;

    public Hotel(String id, String name, String address, String city, String country, double rating, int defaultOverbookPercent, String cancellationPolicyId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.rating = rating;
        this.isActive = true;
        this.defaultOverbookPercent = defaultOverbookPercent;
        this.cancellationPolicyId = cancellationPolicyId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public double getRating() { return rating; }
    public boolean isActive() { return isActive; }
    public int getDefaultOverbookPercent() { return defaultOverbookPercent; }
    public void setDefaultOverbookPercent(int percent) { this.defaultOverbookPercent = percent; }
    public String getCancellationPolicyId() { return cancellationPolicyId; }
    public void setCancellationPolicyId(String cancellationPolicyId) { this.cancellationPolicyId = cancellationPolicyId; }

    @Override
    public String toString() {
        return "Hotel[" + id + " | '" + name + "' | " + city + ", " + country + " | Rating: " + rating + "★]";
    }
}
