package domain;

import java.time.LocalDate;

public class NightlyPrice {
    private final LocalDate date;
    private final long priceMinor; // in paisa/cents

    public NightlyPrice(LocalDate date, long priceMinor) {
        this.date = date;
        this.priceMinor = priceMinor;
    }

    public LocalDate getDate() { return date; }
    public long getPriceMinor() { return priceMinor; }
    public double getPriceRupees() { return priceMinor / 100.0; }

    @Override
    public String toString() {
        return date + ": ₹" + (priceMinor / 100.0);
    }
}
