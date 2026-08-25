package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DateRange {
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;

    public DateRange(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Check-in and Check-out dates cannot be null.");
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be strictly after Check-in date.");
        }
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }

    public int getNumberOfNights() {
        return (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    public List<LocalDate> getDates() {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = checkInDate;
        while (current.isBefore(checkOutDate)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }

    public boolean overlaps(DateRange other) {
        return this.checkInDate.isBefore(other.checkOutDate) && other.checkInDate.isBefore(this.checkOutDate);
    }

    @Override
    public String toString() {
        return "[" + checkInDate + " to " + checkOutDate + " (" + getNumberOfNights() + " nights)]";
    }
}
