import java.util.*;

/**
 * Demonstrates a clean, modular Low-Level Design (LLD) for a User Authentication System.
 * Highlights:
 * 1. Domain Modeling (User entity)
 * 2. Interface Abstractions (UserRepository, PasswordEncoder, NotificationService)
 * 3. Separation of Concerns (Service Layer vs Repository vs Notification)
 * 4. SOLID principles & testability via Dependency Injection
 */
public class LoginSystemLLDExample {

    // ==========================================
    // 1. Domain Entities
    // ==========================================
    static class User {
        private final String userId;
        private final String email;
        private final String passwordHash;

        public User(String userId, String email, String passwordHash) {
            this.userId = userId;
            this.email = email;
            this.passwordHash = passwordHash;
        }

        public String getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        @Override
        public String toString() {
            return "User[ID=" + userId + ", Email=" + email + "]";
        }
    }

    // ==========================================
    // 2. Interfaces (Abstractions)
    // ==========================================
    interface UserRepository {
        Optional<User> findByEmail(String email);
        void save(User user);
    }

    interface PasswordEncoder {
        String encode(String rawPassword);
        boolean matches(String rawPassword, String encodedPassword);
    }

    interface NotificationService {
        void sendNotification(String recipient, String message);
    }

    // ==========================================
    // 3. Concrete Implementations
    // ==========================================
    static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> userMap = new HashMap<>();

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.ofNullable(userMap.get(email.toLowerCase()));
        }

        @Override
        public void save(User user) {
            userMap.put(user.getEmail().toLowerCase(), user);
        }
    }

    static class SimplePasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(String rawPassword) {
            return "ENC_" + Integer.toHexString(rawPassword.hashCode());
        }

        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
        }
    }

    static class EmailNotificationService implements NotificationService {
        @Override
        public void sendNotification(String recipient, String message) {
            System.out.println("📧 [Email to: " + recipient + "] " + message);
        }
    }

    // ==========================================
    // 4. Core Service (Business Logic)
    // ==========================================
    static class AuthService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final NotificationService notificationService;

        public AuthService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           NotificationService notificationService) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
            this.notificationService = notificationService;
        }

        public User signUp(String email, String password) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("User with email " + email + " already exists.");
            }
            String encodedPassword = passwordEncoder.encode(password);
            String userId = "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            User user = new User(userId, email, encodedPassword);
            userRepository.save(user);
            notificationService.sendNotification(email, "Welcome! Your account has been registered.");
            return user;
        }

        public boolean login(String email, String password) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                System.out.println("❌ Login failed: User not found.");
                return false;
            }
            User user = userOpt.get();
            boolean success = passwordEncoder.matches(password, user.getPasswordHash());
            if (success) {
                System.out.println("✅ Login successful for: " + email);
            } else {
                System.out.println("❌ Login failed: Incorrect password.");
            }
            return success;
        }

        public void forgotPassword(String email) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                String tempToken = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                notificationService.sendNotification(email, "Password reset requested. Verification OTP: " + tempToken);
            } else {
                System.out.println("⚠️ Password reset skipped: Email " + email + " not registered.");
            }
        }
    }

    // ==========================================
    // 5. Driver / Demonstration
    // ==========================================
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("🔐 USER AUTHENTICATION SYSTEM - LLD DEMO");
        System.out.println("==================================================");

        // Dependency Injection Setup
        UserRepository userRepo = new InMemoryUserRepository();
        PasswordEncoder passwordEncoder = new SimplePasswordEncoder();
        NotificationService notificationService = new EmailNotificationService();

        AuthService authService = new AuthService(userRepo, passwordEncoder, notificationService);

        // 1. Sign Up User
        System.out.println("\n--- Step 1: User Registration (Sign Up) ---");
        User alice = authService.signUp("alice@example.com", "Password@123");
        System.out.println("Created: " + alice);

        // 2. Successful Login
        System.out.println("\n--- Step 2: Valid Login ---");
        authService.login("alice@example.com", "Password@123");

        // 3. Failed Login (Wrong Password)
        System.out.println("\n--- Step 3: Failed Login (Wrong Password) ---");
        authService.login("alice@example.com", "WrongPassword");

        // 4. Forgot Password Flow
        System.out.println("\n--- Step 4: Forgot Password OTP ---");
        authService.forgotPassword("alice@example.com");

        System.out.println("\n==================================================");
        System.out.println("✅ DEMO COMPLETED SUCCESSFULLY");
        System.out.println("==================================================");
    }
}
