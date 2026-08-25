package repository.impl;

import domain.AdminUser;
import repository.AdminUserRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AdminUserRepositoryImpl implements AdminUserRepository {
    private final Map<String, AdminUser> admins = new ConcurrentHashMap<>();

    @Override
    public AdminUser save(AdminUser adminUser) {
        admins.put(adminUser.getId(), adminUser);
        return adminUser;
    }

    @Override
    public Optional<AdminUser> findById(String adminId) {
        return Optional.ofNullable(admins.get(adminId));
    }
}
