package repository.impl;

import domain.Room;
import repository.RoomRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RoomRepositoryImpl implements RoomRepository {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    @Override
    public Room save(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    @Override
    public Optional<Room> findById(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    @Override
    public List<Room> findByHotelAndType(String hotelId, String roomTypeId) {
        return rooms.values().stream()
                .filter(r -> r.getHotelId().equals(hotelId) && r.getRoomTypeId().equals(roomTypeId) && r.isActive())
                .collect(Collectors.toList());
    }
}
