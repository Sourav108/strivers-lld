package repository.impl;

import domain.Hotel;
import repository.HotelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HotelRepositoryImpl implements HotelRepository {
    private final Map<String, Hotel> hotels = new ConcurrentHashMap<>();

    @Override
    public Hotel save(Hotel hotel) {
        hotels.put(hotel.getId(), hotel);
        return hotel;
    }

    @Override
    public Optional<Hotel> findById(String hotelId) {
        return Optional.ofNullable(hotels.get(hotelId));
    }

    @Override
    public List<Hotel> findAll() {
        return new ArrayList<>(hotels.values());
    }

    @Override
    public List<Hotel> findByLocation(String city, String country) {
        return hotels.values().stream()
                .filter(h -> city == null || h.getCity().equalsIgnoreCase(city))
                .filter(h -> country == null || h.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
    }
}
