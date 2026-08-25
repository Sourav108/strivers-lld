# Task Management System - Low-Level Design

## 1. Problem Statement

Design a robust, scalable, and extensible **Task Management System** (such as Jira / Asana / Trello) supporting composite task/subtask hierarchies, state machine workflow transitions (`TODO` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `REVIEW` $\rightarrow$ `COMPLETED` $\rightarrow$ `CANCELLED`), multi-channel notifications (Observer Pattern), dynamic multi-criteria search and sorting (Strategy Pattern), and comprehensive audit trails.

---

## 2. Requirements

### Functional Requirements
- **Core Task Management:** Create, update, assign, and delete tasks with attributes: title, description, due date, priority (`LOW`, `MEDIUM`, `HIGH`, `URGENT`), and status.
- **Composite Subtask Hierarchy:** Support recursive subtasks under parent tasks with automatic priority escalation (parent priority bumps if a child has higher priority) and cascade deletion.
- **Workflow State Machine:** Enforce valid status transitions using the State Pattern:
  - `TODO` $\rightarrow$ `IN_PROGRESS`, `CANCELLED`
  - `IN_PROGRESS` $\rightarrow$ `REVIEW`, `CANCELLED`, `TODO`
  - `REVIEW` $\rightarrow$ `COMPLETED`, `IN_PROGRESS`
  - `COMPLETED` $\rightarrow$ `IN_PROGRESS` (Reopen)
  - `CANCELLED` $\rightarrow$ `TODO` (Reactivate)
- **Multi-Channel Change Notifications:** Notify observers (Email, Mobile Push) on status changes, assignments, and priority updates (Observer Pattern).
- **Search & Dynamic Sorting:** Filter tasks by assignee, creator, priority, status, date range, tags, and sort dynamically (by Priority, Due Date, Creation Date) in Ascending/Descending order (Strategy Pattern).
- **Audit Trail & History:** Automatically log all task mutations (`CREATED`, `UPDATED`, `STATUS_CHANGED`, `ASSIGNED`, `PRIORITY_CHANGED`).

### Important Non-Functional Requirements
- **Thread Safety & Data Consistency:** Safe concurrent updates and status changes.
- **Modularity & Extensibility:** Seamlessly plug in new sorting strategies, notification channels, or state transitions without changing core logic.

---

## 3. Package Structure

```
src/
├── controller/
│   ├── TaskAssignmentController.java
│   ├── TaskController.java
│   ├── TaskNotificationController.java
│   └── TaskStateController.java
├── domain/
│   ├── Observer/
│   │   ├── EmailSubscriber.java
│   │   ├── MobileAppSubscriber.java
│   │   └── TaskSubscriber.java       (Interface)
│   ├── state/
│   │   ├── CancelledState.java
│   │   ├── CompletedState.java
│   │   ├── InProgressState.java
│   │   ├── InvalidStateTransitionException.java
│   │   ├── ReviewState.java
│   │   ├── TaskState.java            (Interface)
│   │   └── TodoState.java
│   ├── strategy/
│   │   ├── CreatedDateSortingStrategy.java
│   │   ├── DueDateSortingStrategy.java
│   │   ├── PrioritySortingStrategy.java
│   │   └── TaskSortingStrategy.java  (Interface)
│   ├── ChangeType.java               (Enum)
│   ├── Comment.java
│   ├── DataRange.java
│   ├── Priority.java                 (Enum)
│   ├── Task.java                     (Composite Root)
│   ├── TaskChangeLog.java
│   ├── TaskSearchCriteria.java       (Builder)
│   ├── TaskStatus.java               (Enum)
│   ├── TaskSubscription.java
│   ├── User.java
│   └── UserRole.java                 (Enum)
├── repository/
│   ├── CommentRepository.java
│   ├── TaskChangeLogRepository.java
│   ├── TaskRepository.java           (Interface)
│   ├── TaskRepositoryImpl.java
│   ├── TaskSubscriptionRepository.java
│   ├── UserRepository.java           (Interface)
│   └── UserRepositoryImpl.java
├── service/
│   ├── TaskAssignmentService.java
│   ├── TaskNotificationService.java
│   ├── TaskService.java
│   └── TaskStateService.java
└── main/
    └── TaskManagementSimulation.java (Driver Simulation)
```

---

## 4. Class Responsibilities

| Package | Class / Interface | Responsibility (1 Line) |
|---|---|---|
| `domain` | **`Task`** | Composite root holding title, status, priority, subtasks, subscribers, and state logic. |
| `domain` | **`User`** | User domain entity with username, email, and role (`USER`, `ADMIN`). |
| `domain` | **`Comment`** | Collaboration comment attached to a task. |
| `domain` | **`TaskChangeLog`** | Immutable audit log record capturing task mutations. |
| `domain` | **`TaskSubscription`** | Persistent link between a user and a subscribed task. |
| `domain` | **`TaskSearchCriteria`** | Fluent Builder object encapsulating filter parameters and sort preferences. |
| `domain.state` | **`TaskState`** | State pattern interface defining transition validation and status mapping. |
| `domain.state` | **`TodoState`**, **`InProgressState`**, etc. | Concrete workflow states enforcing legal state jumps. |
| `domain.strategy`| **`TaskSortingStrategy`** | Strategy interface sorting `List<Task>` based on dynamic algorithms. |
| `domain.strategy`| **`PrioritySortingStrategy`**, **`DueDateSortingStrategy`**, etc. | Concrete sorting algorithms (Priority descending, Due Date ascending, etc.). |
| `domain.Observer`| **`TaskSubscriber`** | Observer interface defining subscriber update hooks. |
| `domain.Observer`| **`EmailSubscriber`**, **`MobileAppSubscriber`** | Concrete notification channels (Email alerts & Mobile Push). |
| `repository` | **`TaskRepository`** | In-memory CRUD storage and multi-criteria query engine with cascade delete. |
| `repository` | **`UserRepository`**, **`TaskChangeLogRepository`**, etc. | In-memory data stores for users, change logs, and subscriptions. |
| `service` | **`TaskService`** | Core business logic for creating tasks, subtasks, deleting, and searching. |
| `service` | **`TaskStateService`** | Handles thread-safe state machine transitions and audit logging. |
| `service` | **`TaskAssignmentService`** | Manages assignee updates, validations, and assignment change logs. |
| `service` | **`TaskNotificationService`** | Coordinates observer attachment, event broadcasting, and audit trail retrieval. |
| `controller` | **`TaskController`**, **`TaskStateController`**, etc. | Entry point controllers exposing clean API endpoints. |
| `main` | **`TaskManagementSimulation`** | Complete simulation driver executing all interview test scenarios. |

---

## 5. Design Patterns & SOLID Principles

- **Composite Pattern:**
  - `Task` contains `List<Task> subtasks`, allowing uniform treatment of single tasks and nested subtask hierarchies with recursive operations (`getAllSubtasks()`, priority escalation).
- **State Pattern:**
  - `TaskState` encapsulates lifecycle transitions (`TODO` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `REVIEW` $\rightarrow$ `COMPLETED`), preventing illegal jumps via `InvalidStateTransitionException`.
- **Strategy Pattern:**
  - `TaskSortingStrategy` decouples sorting logic from search querying, allowing dynamic runtime selection between `PrioritySortingStrategy`, `DueDateSortingStrategy`, and `CreatedDateSortingStrategy`.
- **Observer Pattern:**
  - `Task` notifies registered `TaskSubscriber` instances (`EmailSubscriber`, `MobileAppSubscriber`) whenever assignments, statuses, or priorities change.
- **Builder Pattern:**
  - `TaskSearchCriteria` provides a fluent API for assembling complex search filters.
- **Single Responsibility Principle (SRP):**
  - Services are isolated by domain concern (`TaskService`, `TaskStateService`, `TaskAssignmentService`, `TaskNotificationService`).
- **Open/Closed Principle (OCP):**
  - New states, sorting strategies, and notification channels can be introduced without modifying existing classes.

---

## 6. Main Flows

### Flow 1: Subtask Creation & Priority Escalation
```
TaskController.addSubtask(parentTaskId: 100, subtaskId: 102, priority: URGENT)
  -> TaskService retrieves Parent Task #100 (Priority: MEDIUM)
  -> ParentTask.addSubtask(Subtask #102)
  -> ParentTask auto-escalates priority: MEDIUM -> URGENT
  -> TaskNotificationService logs audit event
```

### Flow 2: Status Transition & Observer Notification
```
TaskStateController.updateTaskStatus(taskId: 100, IN_PROGRESS)
  -> TaskStateService validates transition via current TodoState.canTransitionTo(IN_PROGRESS) -> TRUE
  -> Task state updates to InProgressState
  -> Task broadcasts to all subscribers:
     -> EmailSubscriber sends alert to alice@company.com
     -> MobileAppSubscriber sends push to Bob's iPhone
  -> TaskChangeLog records mutation
```

---

## 7. Edge Cases Handled

1. **Invalid State Transition:** Attempting to jump directly from `TODO` $\rightarrow$ `COMPLETED` throws `InvalidStateTransitionException`.
2. **Cascade Deletion:** Deleting a parent task recursively removes all child subtasks to prevent orphaned records.
3. **Priority Inconsistency:** If a child subtask is created with higher priority than its parent, the parent task auto-escalates to match.
4. **Concurrent Modifications:** Task state transitions and assignments use synchronized blocks and thread-safe collections (`CopyOnWriteArrayList`, `ConcurrentHashMap`).

---

## 8. How to Run

Compile and execute from the `09-Task-Management-System-Design` directory:

```bash
# Compile all packaged Java source files
javac -d bin $(find src -name "*.java")

# Run the complete demonstration driver
java -cp bin main.TaskManagementSimulation
```

---

## 12. Interview Thinking

### How I Would Explain This in an Interview
1. **Step 1 (Clarify Requirements):** Focus on task CRUD, subtask hierarchy, state machine transitions, multi-channel notifications, and flexible search/sorting.
2. **Step 2 (Identify Core Entities):** `Task`, `User`, `Comment`, `TaskChangeLog`, `TaskSubscription`.
3. **Step 3 (Select Design Patterns):**
   - **Composite Pattern** for subtask hierarchy.
   - **State Pattern** for workflow lifecycle validation.
   - **Strategy Pattern** for dynamic sorting.
   - **Observer Pattern** for multi-channel notifications.
4. **Step 4 (Explain Edge Cases):** Cascade deletion of subtasks, parent priority auto-escalation, and transition enforcement.

### Likely Interviewer Follow-up Questions
1. *How would you handle Task Dependencies (e.g. Task B is blocked by Task A)?*
   - **Answer:** Add `List<Integer> blockedByTaskIds` to `Task`. In `TaskStateService`, prevent transitioning to `IN_PROGRESS` until all prerequisite tasks reach `COMPLETED` state.
2. *How do you support full-text fuzzy search across millions of tasks?*
   - **Answer:** Integrate an inverted index (or Elasticsearch adapter) in `TaskRepository` for keyword token matching across title and description fields.

### Trade-offs
- **Composite Pattern vs Ad-hoc Parent ID List:** Using the Composite Pattern allows treating parent tasks and subtasks uniformly with recursive traversal methods (`getAllSubtasks()`), making cascade operations clean and maintainable.

---

## 🎯 Quick Summary

- **Problem:** Design a Jira/Asana-like task management system with subtasks, workflow state machines, notifications, and sorting.
- **Core Classes:** `Task`, `TaskState` (`TodoState`, `InProgressState`, etc.), `TaskSortingStrategy`, `TaskSubscriber`, `TaskService`.
- **Main Flow:** Create Task $\rightarrow$ Add Subtasks $\rightarrow$ Assign User $\rightarrow$ Transition States $\rightarrow$ Notify Observers $\rightarrow$ Log Audit Trail.
- **Important Design:** Composite Pattern for task tree; State Pattern for status workflow; Strategy Pattern for sorting; Observer Pattern for alerts.
- **Edge Cases:** Invalid transition traps (`InvalidStateTransitionException`), parent priority escalation, and recursive cascade deletion.
- **LLD Takeaway:** Combine Composite, State, Strategy, and Observer patterns to build an enterprise-grade collaborative workflow engine.
- **Memorable Rule:** *"Tree with Composite, transition with State, sort with Strategy, and broadcast with Observer."*
