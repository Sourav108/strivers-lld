# Logging Framework - Low-Level Design

## 1. Problem Statement

Design an extensible, configurable, high-performance, and thread-safe **Logging Framework** in Java (similar to Log4j / SLF4J / Logback) capable of capturing log messages with severity levels, routing them through customizable formatting pipelines, filtering messages dynamically, and dispatching them to multiple output destinations (Console, File, Database) concurrently.

---

## 2. Requirements

### Functional Requirements
- **5 Severity Levels with Priority Hierarchy:** `DEBUG (1)` < `INFO (2)` < `WARNING (3)` < `ERROR (4)` < `FATAL (5)`. Only messages with priority $\ge$ configured threshold are logged.
- **Log Message Structure:** Timestamp, LogLevel, Message string, and optional Source (Class/Method name).
- **Multiple Output Destinations (Appenders):** Support writing to Console, File, or external sinks simultaneously.
- **Pluggable Message Formatting:** Support `SimpleFormatter` and `DetailedFormatter` (including timestamps, sources, and thread IDs).
- **Chain of Filters:** Inspect and drop messages before formatting/appending using configurable criteria (e.g. `LevelFilter`, `SourceFilter`).
- **Dynamic Reconfiguration:** Change log level and attach/remove appenders at runtime.

### Important Non-Functional Requirements
- **Thread Safety:** Concurrent multi-threaded logging without race conditions, corrupted buffers, or mixed line outputs.
- **Low Latency & High Throughput:** Minimal performance overhead on application threads.
- **Extensibility (Open/Closed Principle):** Add new appenders (e.g. AWS CloudWatch, Kafka) or formatters without altering core logger code.

---

## 3. Core Entities

- **`LogLevel` (Enum):** Priority enumeration (`DEBUG(1)` to `FATAL(5)`) with `isGreaterOrEqual()` comparison.
- **`LogMessage` (Domain Entity):** Immutable data holder constructed via the **Builder Pattern**.
- **`LogFormatter` (Strategy Interface):** Defines layout rendering contracts (`SimpleFormatter`, `DetailedFormatter`).
- **`LogAppender` (Strategy Interface):** Defines output destination contracts (`ConsoleAppender`, `FileAppender`).
- **`LogFilter` (Chain of Responsibility Interface):** Defines filtering predicates (`LevelFilter`, `SourceFilter`).
- **`Logger` (Core Orchestrator):** Manages appenders, filters, and logging thresholds.

---

## 4. Main Use Cases

1. **Log Level Filtering:** When set to `WARNING`, skip `DEBUG` and `INFO`, but record `WARNING`, `ERROR`, and `FATAL`.
2. **Multi-Destination Appending:** Route an error message to both the Console and an asynchronous File Appender.
3. **Custom Detailed Formatting:** Format log messages with timestamp, source class, and thread name.
4. **Source Package Filtering:** Restrict audit logging strictly to packages matching `com.takeuforward.*`.
5. **Concurrent Multi-Threaded Logging:** Multiple worker threads safely writing simultaneously.

---

## 5. Class Responsibilities

| Class / Interface | Responsibility (1 Line) |
|---|---|
| **`LogLevel`** | Encapsulates 5 severity priorities and provides ordinal comparison logic. |
| **`LogMessage`** | Immutable entity encapsulating timestamp, severity level, message body, and source. |
| **`LogFormatter`** | Strategy interface converting raw `LogMessage` objects into formatted output strings. |
| **`SimpleFormatter`** | Standard layout: `[LEVEL] [TIMESTAMP] - MESSAGE`. |
| **`DetailedFormatter`** | Comprehensive layout: `[LEVEL] [TIMESTAMP] [SOURCE] [THREAD] - MESSAGE`. |
| **`LogAppender`** | Strategy interface defining destination sink output operations. |
| **`ConsoleAppender`** | Thread-safe writer outputting to standard `System.out` / `System.err`. |
| **`FileAppender`** | Thread-safe writer appending logs to a designated filesystem path with console fallback. |
| **`LogFilter`** | Chain of Responsibility interface determining whether a message should proceed. |
| **`LevelFilter`** | Rejects messages below a minimum severity threshold. |
| **`SourceFilter`** | Rejects messages whose source does not match an allowed namespace prefix. |
| **`Logger`** | Thread-safe central facade coordinating level checks, filter chains, and appender dispatches. |

---

## 6. Class Relationships

```
                             +-------------------+
                             |     <<Enum>>      |
                             |     LogLevel      |
                             +-------------------+
                                       ^
                                       |
+-------------------+        +-------------------+        +--------------------+
|    LogMessage     | -----> |      Logger       | -----> |   <<Interface>>    |
| (Built via Builder)|        | (Central Facade)  |        |     LogFilter      |
+-------------------+        +-------------------+        +--------------------+
                                       |                            ^
                                       v                            |
                             +-------------------+        +---------+----------+
                             |   <<Interface>>   |        | LevelFilter        |
                             |    LogAppender    |        | SourceFilter       |
                             +-------------------+        +--------------------+
                                       |
                    +------------------+------------------+
                    |                                     |
                    v                                     v
          +-------------------+                 +-------------------+
          |  ConsoleAppender  |                 |   FileAppender    |
          +-------------------+                 +-------------------+
                    |                                     |
                    +------------------+------------------+
                                       | uses
                                       v
                             +-------------------+
                             |   <<Interface>>   |
                             |   LogFormatter    |
                             +-------------------+
                                  /         \
                                 /           \
                 +-------------------+   +--------------------+
                 |  SimpleFormatter  |   | DetailedFormatter  |
                 +-------------------+   +--------------------+
```

---

## 7. Design

### Important Design Decisions
1. **Separation of Destination and Layout:** Decoupled `LogAppender` (where to write) from `LogFormatter` (how to format), allowing any appender to use any formatter dynamically.
2. **Immutable `LogMessage` with Builder:** Prevents message corruption as the object passes through multiple asynchronous appenders and filters.
3. **Thread Safety without Global Locks:** Used `CopyOnWriteArrayList` for appender/filter registrations and fine-grained synchronization in appenders to prevent thread contention bottlenecks.

### SOLID Principles
- **SRP (Single Responsibility):** Formatters only format; Appenders only write; Filters only inspect; Logger only orchestrates.
- **OCP (Open/Closed Principle):** Add new appenders (e.g. `KafkaAppender`) or formatters (`JsonFormatter`) without changing 1 line of existing code.
- **LSP (Liskov Substitution):** All formatters, appenders, and filters are 100% interchangeable.
- **ISP (Interface Segregation):** Clean, minimal interfaces (`LogFormatter`, `LogAppender`, `LogFilter`).
- **DIP (Dependency Inversion):** `Logger` depends purely on abstractions, not concrete console/file writers.

### Design Patterns
- **Strategy Pattern:** Used for interchangeable output sinks (`LogAppender`) and layouts (`LogFormatter`).
- **Chain of Responsibility Pattern:** Used for sequential filter execution (`LogFilter`).
- **Builder Pattern:** Used for clean instantiation of `LogMessage`.

---

## 8. Main Flows

### Flow 1: Standard Application Logging
```
App calls logger.info("User registered", "UserService")
  -> Logger checks if INFO >= rootLevel (true)
  -> Builds immutable LogMessage
  -> Passes message to Filter Chain (LevelFilter & SourceFilter both return true)
  -> Iterates through Appenders (ConsoleAppender & FileAppender)
  -> Each Appender invokes its assigned Formatter (DetailedFormatter.format(message))
  -> Thread-safe write to output destination
```

### Flow 2: Log Filtering & Level Dropping
```
Logger level configured to WARNING
App calls logger.debug("Cache miss")
  -> Logger checks: DEBUG (1) >= WARNING (3) -> FALSE
  -> Drops message immediately (Zero allocation overhead)
```

---

## 9. Edge Cases

1. **Multiple Concurrent Threads:** Handled using `synchronized` blocks inside appenders and `CopyOnWriteArrayList` for appender lists, ensuring zero interleaved text.
2. **File System Outage / Full Disk:** `FileAppender` catches `IOException` and gracefully falls back to `System.err` console output without crashing the application.
3. **Null / Blank Log Messages:** `LogMessage.Builder` validates input and throws `IllegalArgumentException` on invalid payloads.
4. **Third-Party Noise / Package Flooding:** `SourceFilter` blocks log messages from external libraries.

---

## 10. How the Code Works

1. `Logger.getLogger("ServiceName")` retrieves or instantiates a named logger.
2. `logger.setLevel(LogLevel.WARNING)` sets the threshold.
3. `logger.addAppender(new ConsoleAppender(LogLevel.DEBUG, new DetailedFormatter()))` attaches output destinations.
4. When `logger.error("DB Timeout")` is called, the framework constructs a `LogMessage`, passes it through the filter chain, formats it, and outputs to the console and log files.

---

## 11. How to Run

Compile and execute the self-contained simulation from the `03-Logging-Framework-Design` directory:

```bash
# Compile all source files
javac -d bin src/*.java

# Run the simulation driver
java -cp bin Main
```

---

## 12. Interview Thinking

### How I Would Explain This in an Interview
1. **Step 1 (Clarify):** Confirm supported log levels (5 levels), destinations required (Console/File), formatting flexibility, and thread-safety requirements.
2. **Step 2 (Entities):** Propose `LogLevel` enum, immutable `LogMessage`, `LogAppender` interface, `LogFormatter` interface, and `LogFilter` interface.
3. **Step 3 (Patterns):** Explain why the **Strategy Pattern** fits Appenders/Formatters and **Chain of Responsibility** fits Filters.
4. **Step 4 (Concurrency):** Emphasize thread safety using thread-safe collections and synchronized appender output streams.

### Likely Interviewer Follow-up Questions
1. *How would you support Asynchronous Non-Blocking Logging?*
   - **Answer:** Introduce an `AsyncAppender` backed by a `BlockingQueue` (or LMAX Disruptor ring buffer). Application threads enqueue log events without waiting for disk I/O, while a background worker thread polls the queue and writes in batches.
2. *How do you handle log file rolling / rotation?*
   - **Answer:** Implement a `RollingFileAppender` with a `SizeBasedTriggeringPolicy` (e.g. roll at 50MB) and `TimeBasedRollingPolicy` (e.g. compress daily logs into `app-YYYY-MM-DD.gz`).

### Trade-offs
- **Synchronous vs Asynchronous Appenders:** Synchronous writing is simpler and guarantees logs are persisted before crashes, but introduces I/O latency to application threads. Asynchronous writing maximizes throughput but risks losing in-flight queue messages on abrupt JVM crashes.

---

## 🎯 Quick Summary

- **Problem:** Design an extensible, thread-safe logging framework with multi-level filtering and pluggable destinations.
- **Core Classes:** `Logger`, `LogLevel`, `LogMessage`, `LogAppender`, `LogFormatter`, `LogFilter`.
- **Main Flow:** `logger.log()` $\rightarrow$ Level Check $\rightarrow$ Filter Chain $\rightarrow$ Appender Dispatch $\rightarrow$ Formatter Rendering $\rightarrow$ Output Write.
- **Important Design:** Strategy Pattern for Appenders and Formatters; Chain of Responsibility for Filters.
- **Edge Cases:** Thread concurrency locks, file write failure fallback to console, and builder input validation.
- **LLD Takeaway:** Decouple output destinations from message formatting to maximize flexibility and adhere to the Open/Closed Principle.
- **Memorable Rule:** *"Format with strategies, route to appenders, filter through chains, and lock down concurrency."*
