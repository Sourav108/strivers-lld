package repository;

import domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(int id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
}
