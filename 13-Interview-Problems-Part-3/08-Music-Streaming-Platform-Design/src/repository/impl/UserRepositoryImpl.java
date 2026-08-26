package repository.impl;

import domain.User;
import repository.UserRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new ConcurrentHashMap<>();

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
}
