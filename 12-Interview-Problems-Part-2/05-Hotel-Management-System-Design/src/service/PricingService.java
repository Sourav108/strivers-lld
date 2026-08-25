package service;

import domain.DateRange;
import domain.NightlyPrice;
import domain.RoomType;
import domain.SeasonalPrice;
import repository.RoomTypeRepository;
import repository.SeasonalPriceRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PricingService {
    private final RoomTypeRepository roomTypeRepository;
    private final SeasonalPriceRepository seasonalPriceRepository;

    public PricingService(RoomTypeRepository roomTypeRepository, SeasonalPriceRepository seasonalPriceRepository) {
        this.roomTypeRepository = roomTypeRepository;
        this.seasonalPriceRepository = seasonalPriceRepository;
    }

    public List<NightlyPrice> rateStay(String hotelId, String roomTypeId, DateRange range) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new IllegalArgumentException("RoomType #" + roomTypeId + " not found."));

        List<NightlyPrice> rates = new ArrayList<>();
        for (LocalDate date : range.getDates()) {
            Optional<SeasonalPrice> seasonal = seasonalPriceRepository.findByDate(hotelId, roomTypeId, date);
            long priceMinor = seasonal.map(SeasonalPrice::getPriceMinor).orElse(roomType.getBasePriceMinor());
            rates.add(new NightlyPrice(date, priceMinor));
        }
        return rates;
    }

    public long computeTotal(List<NightlyPrice> nightly) {
        long total = 0;
        for (NightlyPrice np : nightly) {
            total += np.getPriceMinor();
        }
        return total;
    }

    public double computeAveragePricePerNight(List<NightlyPrice> nightly) {
        if (nightly == null || nightly.isEmpty()) return 0.0;
        return (double) computeTotal(nightly) / nightly.size();
    }
}
