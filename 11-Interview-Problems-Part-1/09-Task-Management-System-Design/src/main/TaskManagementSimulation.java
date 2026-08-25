package main;

import controller.TaskAssignmentController;
import controller.TaskController;
import controller.TaskNotificationController;
import controller.TaskStateController;
import domain.*;
import domain.Observer.EmailSubscriber;
import domain.Observer.MobileAppSubscriber;
import domain.state.InvalidStateTransitionException;
import repository.*;
import service.TaskAssignmentService;
import service.TaskNotificationService;
import service.TaskService;
import service.TaskStateService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TaskManagementSimulation: Complete End-to-End Simulation of the Task Management System
 * 
 * Demonstrates:
 * 1. Composite Pattern: Recursive Task / Subtask Hierarchy & Automatic Priority Escalation
 * 2. State Pattern: Task Workflow State Machine (TODO -> IN_PROGRESS -> REVIEW -> COMPLETED)
 * 3. Observer Pattern: Multi-Channel Real-Time Notifications (Email & Mobile Push)
 * 4. Strategy Pattern: Swappable Sorting Algorithms (Priority, Due Date, Creation Date)
 * 5. Repository Pattern & Audit Trail: Complete Change Log History
 * 6. Cascade Deletion: Deleting parent task recursively clears all subtasks
 */

public class TaskManagementSimulation {
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("📋 TASK MANAGEMENT SYSTEM - LLD INTERVIEW DEMONSTRATION");
        System.out.println("=================================================================");

        // --- 1. INITIALIZE REPOSITORIES ---
        TaskRepository taskRepo = new TaskRepositoryImpl();
        UserRepository userRepo = new UserRepositoryImpl();
        CommentRepository commentRepo = new CommentRepository();
        TaskChangeLogRepository changeLogRepo = new TaskChangeLogRepository();
        TaskSubscriptionRepository subscriptionRepo = new TaskSubscriptionRepository();

        // --- 2. SEED USERS ---
        User alice = new User(1, "alice_lead", "alice@company.com", UserRole.ADMIN);
        User bob = new User(2, "bob_dev", "bob@company.com", UserRole.USER);
        User charlie = new User(3, "charlie_qa", "charlie@company.com", UserRole.USER);
        userRepo.save(alice);
        userRepo.save(bob);
        userRepo.save(charlie);

        // --- 3. INITIALIZE SERVICES ---
        TaskNotificationService notificationService = new TaskNotificationService(taskRepo, changeLogRepo, subscriptionRepo);
        TaskService taskService = new TaskService(taskRepo, notificationService);
        TaskStateService taskStateService = new TaskStateService(taskRepo, notificationService);
        TaskAssignmentService assignmentService = new TaskAssignmentService(taskRepo, userRepo, notificationService);

        // --- 4. INITIALIZE CONTROLLERS ---
        TaskController taskController = new TaskController(taskService);
        TaskStateController stateController = new TaskStateController(taskStateService);
        TaskAssignmentController assignmentController = new TaskAssignmentController(assignmentService);
        TaskNotificationController notificationController = new TaskNotificationController(notificationService);

        // =========================================================================
        // SCENARIO 1: CREATE COMPOSITE TASKS & SUBTASKS (Composite Pattern)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ SCENARIO 1: Composite Task & Subtask Creation Hierarchy");
        System.out.println("-----------------------------------------------------------");

        // Create Parent Epic Task
        Task epicTask = taskController.createTask(
                100, "Release Payment Gateway V2", "Upgrade Razorpay/Stripe integrations",
                LocalDateTime.now().plusDays(10), Priority.MEDIUM, alice.getId()
        );

        // Create Nested Subtasks
        Task subtask1 = taskController.addSubtask(
                100, 101, "Implement Webhook Security", "Add HMAC-SHA256 signature verification",
                LocalDateTime.now().plusDays(3), Priority.HIGH, alice.getId()
        );

        Task subtask2 = taskController.addSubtask(
                100, 102, "Performance Benchmark", "Ensure p99 latency < 50ms",
                LocalDateTime.now().plusDays(5), Priority.URGENT, alice.getId()
        );

        System.out.println("📌 Parent Task After Subtask Escalation: " + epicTask);

        // =========================================================================
        // SCENARIO 2: ATTACH OBSERVERS (Observer Pattern)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ SCENARIO 2: Attach Notification Observers (Email & Push)");
        System.out.println("-----------------------------------------------------------");

        notificationController.subscribeToTask(100, alice.getId(), new EmailSubscriber(alice.getEmail()));
        notificationController.subscribeToTask(100, bob.getId(), new MobileAppSubscriber("DEVICE_TOKEN_BOB_IPHONE"));

        // =========================================================================
        // SCENARIO 3: ASSIGN TASK & STATE TRANSITIONS (State Pattern)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ SCENARIO 3: Task Assignment & State Transitions");
        System.out.println("-----------------------------------------------------------");

        // Assign Epic to Bob
        assignmentController.assignTask(100, bob.getId(), alice.getId());

        // Workflow Progress: TODO -> IN_PROGRESS -> REVIEW -> COMPLETED
        stateController.updateTaskStatus(100, TaskStatus.IN_PROGRESS, bob.getId());
        stateController.updateTaskStatus(100, TaskStatus.REVIEW, bob.getId());
        stateController.updateTaskStatus(100, TaskStatus.COMPLETED, charlie.getId());

        // =========================================================================
        // SCENARIO 4: STATE PATTERN SAFETY (Invalid Transition Trap)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ SCENARIO 4: State Pattern Enforcement (Invalid Transition Trap)");
        System.out.println("-----------------------------------------------------------");

        // Subtask 1 is currently in TODO. Try skipping to COMPLETED directly!
        try {
            System.out.println("⚠️ Attempting invalid direct jump on Subtask #101: TODO -> COMPLETED...");
            stateController.updateTaskStatus(101, TaskStatus.COMPLETED, bob.getId());
        } catch (InvalidStateTransitionException e) {
            System.out.println("   🛡️ Caught Expected State Violation -> " + e.getMessage());
        }

        // =========================================================================
        // SCENARIO 5: SEARCH & DYNAMIC SORTING (Strategy Pattern)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("5️⃣ SCENARIO 5: Task Search & Strategy Pattern Sorting");
        System.out.println("-----------------------------------------------------------");

        // Create standalone task for comparison
        taskController.createTask(
                200, "Fix Landing Page Typos", "Fix spelling on pricing page",
                LocalDateTime.now().plusDays(1), Priority.LOW, alice.getId()
        );

        // Query 1: Sort by Priority (Descending: URGENT -> HIGH -> MEDIUM -> LOW)
        System.out.println("\n🔍 Sorting all tasks by PRIORITY (Descending):");
        TaskSearchCriteria priorityCriteria = new TaskSearchCriteria().sortBy("priority").sortOrder("desc");
        List<Task> prioritySorted = taskController.searchTasks(priorityCriteria);
        for (Task t : prioritySorted) {
            System.out.println("   " + t);
        }

        // Query 2: Sort by Due Date (Ascending: Earliest first)
        System.out.println("\n🔍 Sorting all tasks by DUE DATE (Ascending):");
        TaskSearchCriteria dueDateCriteria = new TaskSearchCriteria().sortBy("dueDate").sortOrder("asc");
        List<Task> dueDateSorted = taskController.searchTasks(dueDateCriteria);
        for (Task t : dueDateSorted) {
            System.out.println("   Task #" + t.getId() + " - Due: " + t.getDueDate().toLocalDate() + " | " + t.getTitle());
        }

        // =========================================================================
        // SCENARIO 6: AUDIT TRAIL & HISTORY
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("6️⃣ SCENARIO 6: Audit Trail & Task History Logs");
        System.out.println("-----------------------------------------------------------");

        List<TaskChangeLog> history = notificationController.getTaskHistory(100);
        for (TaskChangeLog log : history) {
            System.out.println("   " + log);
        }

        // =========================================================================
        // SCENARIO 7: CASCADE DELETION (Composite Pattern)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("7️⃣ SCENARIO 7: Cascade Deletion of Parent & Subtasks");
        System.out.println("-----------------------------------------------------------");

        taskController.deleteTask(100); // Should cascade delete #101 and #102
        System.out.println("Remaining Tasks in System after cascade deletion:");
        List<Task> remaining = taskController.searchTasks(new TaskSearchCriteria());
        for (Task t : remaining) {
            System.out.println("   " + t);
        }

        System.out.println("\n=================================================================");
        System.out.println("🎯 TASK MANAGEMENT SYSTEM ARCHITECTURE COMPLETE & VERIFIED!");
        System.out.println("=================================================================");
    }
}
