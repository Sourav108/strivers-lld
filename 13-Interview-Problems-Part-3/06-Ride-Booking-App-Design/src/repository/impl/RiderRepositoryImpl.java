package repository.impl;

import domain.Rider;
import repository.RiderRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RiderRepositoryImpl implements RiderRepository {
    private final Map<Integer, Rider> riders = new ConcurrentHashMap<>();

    @Override
    public Optional<Rider> findById(int id) {
        return Optional.ofNullable(riders.get(id));
    }

    @Override
    public Optional<Rider> findByEmail(String email) {
        return riders.values().stream().filter(r -> r.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    @Override
    public Rider save(Rider rider) {
        riders.put(rider.getId(), rider);
        return rider;
    }
}
