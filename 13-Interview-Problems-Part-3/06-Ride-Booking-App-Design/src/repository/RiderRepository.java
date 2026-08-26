package repository;

import domain.Rider;

import java.util.Optional;

public interface RiderRepository {
    Optional<Rider> findById(int id);
    Optional<Rider> findByEmail(String email);
    Rider save(Rider rider);
}
