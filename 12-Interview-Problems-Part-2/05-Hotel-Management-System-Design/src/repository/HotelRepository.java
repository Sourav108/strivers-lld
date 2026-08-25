package repository;

import domain.Hotel;
import java.util.List;
import java.util.Optional;

public interface HotelRepository {
    Hotel save(Hotel hotel);
    Optional<Hotel> findById(String hotelId);
    List<Hotel> findAll();
    List<Hotel> findByLocation(String city, String country);
}
