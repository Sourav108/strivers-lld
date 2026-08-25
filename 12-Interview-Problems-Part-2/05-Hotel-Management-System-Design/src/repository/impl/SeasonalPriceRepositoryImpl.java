package repository.impl;

import domain.SeasonalPrice;
import repository.SeasonalPriceRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SeasonalPriceRepositoryImpl implements SeasonalPriceRepository {
    private final Map<String, SeasonalPrice> prices = new ConcurrentHashMap<>();

    private String buildKey(String hotelId, String roomTypeId, LocalDate date) {
        return hotelId + "_" + roomTypeId + "_" + date.toString();
    }

    @Override
    public SeasonalPrice save(SeasonalPrice seasonalPrice) {
        prices.put(buildKey(seasonalPrice.getHotelId(), seasonalPrice.getRoomTypeId(), seasonalPrice.getDate()), seasonalPrice);
        return seasonalPrice;
    }

    @Override
    public Optional<SeasonalPrice> findByDate(String hotelId, String roomTypeId, LocalDate date) {
        return Optional.ofNullable(prices.get(buildKey(hotelId, roomTypeId, date)));
    }
}
