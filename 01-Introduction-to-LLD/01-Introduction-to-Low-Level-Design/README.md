# 01 - Introduction to Low-Level Design (LLD)

> **Prerequisite:** This course is implemented in **Java** (due to its widespread adoption across large-scale enterprise codebases), but the concepts, design patterns, and principles apply universally to any object-oriented programming language. A solid foundational understanding of **Object-Oriented Programming (OOP)** is the primary prerequisite.

---

## 1. Understanding High-Level Design (HLD) vs Low-Level Design (LLD)

Let us understand the difference with a simple real-life analogy: **Building a House**.

* **High-Level Design (HLD):** Like the architect’s blueprint. It defines where the rooms will be, their dimensions, structural boundaries, and how they connect to one another.
* **Low-Level Design (LLD):** Like the engineering specifications. It specifies where electrical switches and conduits are placed, how the plumbing is routed, what materials and fixtures to use, and exact wiring diagrams.

```
┌───────────────────────────────────────────────────────────┐
│                 High-Level Design (HLD)                   │
│   System Architecture • Microservices • Databases • APIs  │
└─────────────────────────────┬─────────────────────────────┘
                              │
                              ▼
┌───────────────────────────────────────────────────────────┐
│                 Low-Level Design (LLD)                    │
│   Class Diagrams • Methods • Data Structures • Patterns   │
└─────────────────────────────┬─────────────────────────────┘
                              │
                              ▼
┌───────────────────────────────────────────────────────────┐
│                   Actual Code Execution                   │
│      Clean Code • Unit Tests • Production Deployment      │
└───────────────────────────────────────────────────────────┘
```

> [!NOTE]
> **LLD is the detailed planning phase done before writing production code.** It bridges the gap between high-level architectural abstractions and actual source code.

---

## 2. Definition of Low-Level Design

**Low-Level Design (LLD)** (also known as *Detailed Design* or *Object-Oriented Design*) is a crucial phase in the software development lifecycle (SDLC) that focuses on the internal structure, behavior, and relationships of individual system components and modules.

It involves:
1. Identifying **Classes**, **Interfaces**, **Enums**, and **Data Structures**.
2. Specifying **Method Signatures**, access modifiers, and invariants.
3. Defining **Class Relationships** (Association, Aggregation, Composition, Inheritance).
4. Selecting appropriate **Design Patterns** (Creational, Structural, Behavioral) and applying **SOLID Principles**.

---

## 3. Key Characteristics of LLD

### 1. Granular and Code-Level
LLD dives deep into the fine details of how each component functions. It defines exact classes, attributes, methods, and algorithms.
* *Example:* Instead of just stating *"we need user authentication"*, LLD details how it is built — what classes handle authentication (`AuthService`), how password hashing is abstracted (`PasswordEncoder`), how user records are retrieved (`UserRepository`), and how failures/exceptions are propagated.

### 2. Implementation-Focused
LLD acts as a direct, actionable blueprint for software engineers.
* It guides the logic, data flow, and structure of modules.
* It often includes UML class diagrams, sequence diagrams, and pseudocode that visualize interaction flows between objects.

### 3. Applies OOP Principles
LLD heavily leverages Object-Oriented Programming (OOP) concepts:
* **Encapsulation:** Protecting mutable state and exposing clean APIs.
* **Abstraction:** Hiding complex implementation details behind interfaces.
* **Inheritance & Polymorphism:** Enabling swappable, modular components (e.g., a base `NotificationService` interface with `EmailNotificationService` and `SmsNotificationService` implementations).

### 4. Primary Stakeholders
In LLD, stakeholders are primarily the technical engineers and architects directly involved in implementing, reviewing, and maintaining the system:
* **Senior Software Engineers / Developers**
* **Technical Leads & Architects**
* **Engineering Managers**

---

## 4. Difference between HLD and LLD

| Aspect | High-Level Design (HLD) | Low-Level Design (LLD) |
|---|---|---|
| **Purpose** | System overview, service boundaries, and data flow | Detailed class structure, algorithms, and logic |
| **Level of Detail** | Abstract / Macro-level | Highly detailed / Micro-level |
| **Primary Focus** | Architecture, databases, message queues, API gateways | Class diagrams, design patterns, method signatures, concurrency |
| **Outcome** | Architecture diagrams, component interactions | UML class diagrams, sequence diagrams, Java classes |
| **Audience** | Product Managers, Architects, Stakeholders, Developers | Software Engineers, Code Reviewers, Tech Leads |
| **Example** | *"Use a distributed cache and authentication service"* | *"Create `Cache<K,V>` interface with `LRUCache` implementing `put()` and `get()`"* |

---

## 5. Importance of Low-Level Design

Beyond being a critical competency evaluated in senior engineering interviews, LLD is essential for several reasons:

1. **Avoids Costly Rework:** Clearly defined logic and modular abstractions help catch architectural flaws early before hundreds of lines of code are written.
2. **Improves Team Collaboration:** Serves as a shared contract across engineering teams, ensuring unambiguous API boundaries and integration points.
3. **Promotes Scalability & Extensibility:** Decoupled, modular components make adding new features seamless without breaking existing functionality (Open/Closed Principle).
4. **Encourages Clean Code & Best Practices:** Enforces design patterns, error handling, thread safety, and testability.

---

## 6. Coding Example: User Authentication System LLD

A practical example of LLD is modeling a **User Authentication System** with `login()`, `signUp()`, and `forgotPassword()` operations using clean OOP abstractions:

```
                  ┌────────────────────────┐
                  │      AuthService       │
                  └───────────┬────────────┘
                              │
         ┌────────────────────┼────────────────────┐
         ▼                    ▼                    ▼
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│  UserRepository  │ │  PasswordEncoder │ │ NotificationSvc  │
│  (Data Access)   │ │  (Hashing Logic) │ │  (Email / SMS)   │
└──────────────────┘ └──────────────────┘ └──────────────────┘
```

### Complete Java Implementation

```java
import java.util.*;

// Domain Model
class User {
    private final String userId;
    private final String email;
    private final String passwordHash;

    public User(String userId, String email, String passwordHash) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
}

// Abstractions
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

// Concrete Implementations
class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> usersByEmail = new HashMap<>();

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email.toLowerCase()));
    }

    @Override
    public void save(User user) {
        usersByEmail.put(user.getEmail().toLowerCase(), user);
    }
}

class SimplePasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(String rawPassword) {
        return "HASH_" + Integer.toHexString(rawPassword.hashCode());
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}

class EmailNotificationService implements NotificationService {
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("📧 [Email to " + recipient + "] " + message);
    }
}

// Core LLD Service
class AuthService {
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
        String userId = "USR-" + UUID.randomUUID().toString().substring(0, 8);
        User user = new User(userId, email, encodedPassword);
        userRepository.save(user);
        notificationService.sendNotification(email, "Welcome to our platform! Your account has been created.");
        return user;
    }

    public boolean login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    public void forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            String tempResetToken = UUID.randomUUID().toString().substring(0, 6);
            notificationService.sendNotification(email, "Your password reset token is: " + tempResetToken);
        }
    }
}
```

---

## 7. Summary

- **HLD:** Answers **What** the system does at a macro level (components, databases, networks).
- **LLD:** Answers **How** each component is implemented in code (classes, methods, relationships, patterns).
- **Goal:** Transform ambiguous product requirements into clean, modular, extensible, and interview-ready object-oriented code.
