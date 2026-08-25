package controller;

import domain.*;
import repository.*;
import service.BookingService;

public class AdminController {
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final SeasonalPriceRepository seasonalPriceRepository;
    private final CancellationPolicyRepository cancellationPolicyRepository;
    private final BookingService bookingService;

    public AdminController(HotelRepository hotelRepository,
                           RoomTypeRepository roomTypeRepository,
                           RoomRepository roomRepository,
                           SeasonalPriceRepository seasonalPriceRepository,
                           CancellationPolicyRepository cancellationPolicyRepository,
                           BookingService bookingService) {
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.seasonalPriceRepository = seasonalPriceRepository;
        this.cancellationPolicyRepository = cancellationPolicyRepository;
        this.bookingService = bookingService;
    }

    public Hotel createOrUpdateHotel(Hotel hotel) {
        hotelRepository.save(hotel);
        return hotel;
    }

    public RoomType createOrUpdateRoomType(RoomType roomType) {
        roomTypeRepository.save(roomType);
        return roomType;
    }

    public Room addRoom(Room room) {
        roomRepository.save(room);
        return room;
    }

    public SeasonalPrice setSeasonalPrice(SeasonalPrice seasonalPrice) {
        seasonalPriceRepository.save(seasonalPrice);
        System.out.println("📈 [Seasonal Price Set] " + seasonalPrice);
        return seasonalPrice;
    }

    public CancellationPolicy createOrUpdatePolicy(CancellationPolicy policy) {
        cancellationPolicyRepository.save(policy);
        return policy;
    }

    public Booking checkIn(String bookingId, String roomId) {
        return bookingService.checkIn(bookingId, roomId, System.currentTimeMillis());
    }

    public Booking checkOut(String bookingId) {
        return bookingService.checkOut(bookingId, System.currentTimeMillis());
    }
}
