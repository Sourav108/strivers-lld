package repository;

import domain.Room;
import java.util.List;
import java.util.Optional;

public interface RoomRepository {
    Room save(Room room);
    Optional<Room> findById(String roomId);
    List<Room> findByHotelAndType(String hotelId, String roomTypeId);
}
