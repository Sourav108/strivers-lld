package domain;

import java.time.LocalDateTime;

public class DataRange {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public DataRange(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }

    public boolean includes(LocalDateTime date) {
        if (date == null) return false;
        if (startDate != null && date.isBefore(startDate)) return false;
        if (endDate != null && date.isAfter(endDate)) return false;
        return true;
    }
}
