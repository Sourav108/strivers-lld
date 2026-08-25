# Logging Framework - Low-Level Design

## 1. Problem Statement

Design an extensible, configurable, high-performance, and thread-safe **Logging Framework** in Java (similar to Log4j / SLF4J / Logback) capable of capturing log messages with severity levels, routing them through customizable formatting pipelines, filtering messages dynamically, and dispatching them to multiple output destinations (`ConsoleAppender`, `FileAppender`, `DatabaseAppender`) concurrently.

---

## 2. Requirements

### Functional Requirements
- **5 Severity Levels with Priority Hierarchy:** `DEBUG (1)` < `INFO (2)` < `WARNING (3)` < `ERROR (4)` < `FATAL (5)`. Only messages with priority $\ge$ configured threshold are logged.
- **Log Message Structure:** Timestamp, LogLevel, Message string, and optional Source (Class/Method name).
- **Multiple Output Destinations (Appenders):** Support writing to Console, File, or Database destinations simultaneously.
- **Pluggable Message Formatting:** Support `SimpleFormatter` and `DetailedFormatter` (including timestamps, sources, and thread IDs).
- **Chain of Filters:** Inspect and drop messages before formatting/appending using configurable criteria (e.g. `LevelFilter`, `SourceFilter`).
- **Dynamic Reconfiguration:** Change log level and attach/remove appenders at runtime.

### Important Non-Functional Requirements
- **Thread Safety:** Concurrent multi-threaded logging without race conditions, corrupted buffers, or mixed line outputs.
- **Low Latency & High Throughput:** Minimal performance overhead on application threads.
- **Extensibility (Open/Closed Principle):** Add new appenders (e.g. AWS CloudWatch, Kafka) or formatters without altering core logger code.

---

## 3. Package Structure

```
src/
├── appenders/
│   ├── ConsoleAppender.java
│   ├── DatabaseAppender.java
│   └── FileAppender.java
├── core/
│   ├── LogAppender.java       (Interface)
│   ├── LogConfiguration.java
│   ├── LogFilter.java          (Interface)
│   ├── LogFormatter.java       (Interface)
│   ├── Logger.java             (Interface)
│   ├── LoggerImpl.java
│   ├── LogLevel.java           (Enum)
│   └── LogMessage.java
├── filter/
│   ├── LevelFilter.java
│   └── SourceFilter.java
├── formatters/
│   ├── DetailedFormatter.java
│   └── SimpleFormatter.java
└── main/
    └── LoggingDemo.java
```

---

## 4. Class Responsibilities

| Package | Class / Interface | Responsibility (1 Line) |
|---|---|---|
| `core` | **`LogLevel`** | Enum defining severity priorities (`DEBUG(1)` to `FATAL(5)`) with comparison helpers. |
| `core` | **`LogMessage`** | Immutable value object encapsulating timestamp, level, message, and source (built via Builder). |
| `core` | **`LogFormatter`** | Strategy interface converting raw `LogMessage` objects into formatted strings. |
| `core` | **`LogAppender`** | Strategy interface defining destination sink append and level checks. |
| `core` | **`LogFilter`** | Chain of Responsibility interface for filtering messages before dispatch. |
| `core` | **`LogConfiguration`** | Configuration bean holding root logging levels and system preferences. |
| `core` | **`Logger`** | Public contract interface exposing logging methods (`debug`, `info`, `warning`, `error`, `fatal`). |
| `core` | **`LoggerImpl`** | Thread-safe implementation orchestrating level checks, filters, and appenders. |
| `formatters` | **`SimpleFormatter`** | Layout: `[LEVEL] [TIMESTAMP] - MESSAGE`. |
| `formatters` | **`DetailedFormatter`** | Layout: `[LEVEL] [TIMESTAMP] [SOURCE] [THREAD] - MESSAGE`. |
| `appenders` | **`ConsoleAppender`** | Outputs to standard `System.out` or `System.err`. |
| `appenders` | **`FileAppender`** | Writes logs to a designated filesystem path with console fallback. |
| `appenders` | **`DatabaseAppender`** | Stores structured records into database tables with connection outage handling. |
| `filter` | **`LevelFilter`** | Drops messages below a configured minimum severity. |
| `filter` | **`SourceFilter`** | Drops messages whose source does not match an allowed package prefix. |
| `main` | **`LoggingDemo`** | Main simulation driver verifying all scenarios, multi-threading, and error handling. |

---

## 5. Design Patterns & SOLID Principles

- **Strategy Pattern:**
  - `LogAppender` hierarchy (`ConsoleAppender`, `FileAppender`, `DatabaseAppender`) makes output destinations interchangeable.
  - `LogFormatter` hierarchy (`SimpleFormatter`, `DetailedFormatter`) makes layout rendering interchangeable.
- **Chain of Responsibility Pattern:**
  - `LogFilter` chain (`LevelFilter`, `SourceFilter`) evaluates sequential filtering predicates, halting processing early on rejection.
- **Builder Pattern:**
  - `LogMessage.Builder` allows safe, step-by-step construction of immutable log messages.
- **Single Responsibility Principle (SRP):**
  - Appenders only write; Formatters only format; Filters only inspect; Logger only orchestrates.
- **Dependency Inversion Principle (DIP):**
  - High-level `LoggerImpl` depends strictly on interfaces (`LogAppender`, `LogFormatter`, `LogFilter`).

---

## 6. Main Flows

### Flow 1: Standard Application Logging
```
App calls logger.info("User registered", "UserService")
  -> LoggerImpl checks: INFO (2) >= rootLevel (2) -> TRUE
  -> Builds immutable LogMessage via Builder
  -> Passes message to Filter Chain (LevelFilter & SourceFilter return true)
  -> Iterates over Appenders (ConsoleAppender, FileAppender, DatabaseAppender)
  -> Appender checks: isEnabled(INFO) -> TRUE
  -> Appender invokes its Formatter (DetailedFormatter.format(message))
  -> Thread-safe synchronized write to output destination
```

### Flow 2: Log Filtering & Level Dropping
```
Logger level configured to WARNING (3)
App calls logger.debug("Cache miss")
  -> LoggerImpl checks: DEBUG (1) >= WARNING (3) -> FALSE
  -> Drops message immediately (Zero formatting or I/O overhead)
```

---

## 7. Edge Cases Handled

1. **Concurrent Multi-Threaded Logging:** Handled via `synchronized` append methods and `CopyOnWriteArrayList` collections, preventing interleaved or corrupted text.
2. **Database Outage / File Write Errors:** `DatabaseAppender` and `FileAppender` catch exceptions and gracefully route unwritten logs to `System.err` console fallback.
3. **Invalid Input:** `LogMessage.Builder` validates message text, throwing `IllegalArgumentException` on null/blank inputs.
4. **Unwanted Third-Party Noise:** `SourceFilter` discards log messages from unwanted namespaces.

---

## 8. How to Run

Compile and execute from the `03-Logging-Framework-Design` directory:

```bash
# Compile all packaged Java sources
javac -d bin $(find src -name "*.java")

# Run the complete demonstration
java -cp bin main.LoggingDemo
```

---

## 🎯 Quick Summary

- **Problem:** Design an extensible, thread-safe logging framework with multi-level filtering and pluggable destinations.
- **Core Classes:** `LoggerImpl`, `LogLevel`, `LogMessage`, `LogAppender`, `LogFormatter`, `LogFilter`.
- **Main Flow:** `logger.log()` $\rightarrow$ Level Check $\rightarrow$ Filter Chain $\rightarrow$ Appender Dispatch $\rightarrow$ Formatter Rendering $\rightarrow$ Output Write.
- **Important Design:** Strategy Pattern for Appenders and Formatters; Chain of Responsibility for Filters.
- **Edge Cases:** Thread concurrency locks, file/database write failure fallback to console, and builder input validation.
- **LLD Takeaway:** Decouple output destinations from message formatting to maximize flexibility and adhere to the Open/Closed Principle.
- **Memorable Rule:** *"Format with strategies, route to appenders, filter through chains, and lock down concurrency."*
