package service;

import domain.*;
import repository.HotelRepository;
import repository.RoomTypeRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchService {
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final InventoryService inventoryService;
    private final PricingService pricingService;

    public SearchService(HotelRepository hotelRepository, RoomTypeRepository roomTypeRepository,
                         InventoryService inventoryService, PricingService pricingService) {
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.inventoryService = inventoryService;
        this.pricingService = pricingService;
    }

    public List<Hotel> searchHotels(SearchFilter filter) {
        return hotelRepository.findByLocation(filter.getCity(), filter.getCountry());
    }

    public List<RoomTypeAvailability> getAvailability(String hotelId, DateRange range) {
        List<RoomType> roomTypes = roomTypeRepository.findByHotel(hotelId);
        List<RoomTypeAvailability> result = new ArrayList<>();

        for (RoomType rt : roomTypes) {
            boolean isAvailable = inventoryService.checkAvailability(hotelId, rt.getId(), range, 1);
            List<NightlyPrice> rates = pricingService.rateStay(hotelId, rt.getId(), range);
            long totalPrice = pricingService.computeTotal(rates);
            double avgPrice = pricingService.computeAveragePricePerNight(rates);

            result.add(new RoomTypeAvailability(
                    rt.getId(), rt.getName(), rt.getCapacity(), rt.getBedType(),
                    rt.getAmenities(), isAvailable, totalPrice, avgPrice, rates
            ));
        }

        return result;
    }
}
