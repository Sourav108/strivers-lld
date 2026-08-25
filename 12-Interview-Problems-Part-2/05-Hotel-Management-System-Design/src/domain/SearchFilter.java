package domain;

public class SearchFilter {
    private String city;
    private String country;
    private DateRange dateRange;
    private Integer capacity;
    private Long maxPricePerNightMinor;

    public SearchFilter city(String city) { this.city = city; return this; }
    public SearchFilter country(String country) { this.country = country; return this; }
    public SearchFilter dateRange(DateRange dateRange) { this.dateRange = dateRange; return this; }
    public SearchFilter capacity(Integer capacity) { this.capacity = capacity; return this; }
    public SearchFilter maxPricePerNightMinor(Long maxPrice) { this.maxPricePerNightMinor = maxPrice; return this; }

    public String getCity() { return city; }
    public String getCountry() { return country; }
    public DateRange getDateRange() { return dateRange; }
    public Integer getCapacity() { return capacity; }
    public Long getMaxPricePerNightMinor() { return maxPricePerNightMinor; }
}
