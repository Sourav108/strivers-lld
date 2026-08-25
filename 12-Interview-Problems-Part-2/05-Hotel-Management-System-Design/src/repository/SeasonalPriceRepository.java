package repository;

import domain.SeasonalPrice;
import java.time.LocalDate;
import java.util.Optional;

public interface SeasonalPriceRepository {
    SeasonalPrice save(SeasonalPrice seasonalPrice);
    Optional<SeasonalPrice> findByDate(String hotelId, String roomTypeId, LocalDate date);
}
