/**
 * SOLID Principles: Single Responsibility Principle (SRP)
 * 
 * Core Concept: A class should have one, and only one, reason to change.
 * In other words, a class should have only one job, responsibility, and purpose.
 */

// ==========================================
// ❌ BAD DESIGN (Violates SRP: "God Class")
// TUFplusCompiler has 5 distinct responsibilities and 5 reasons to change.
// ==========================================
class BadTUFCompiler {
    public void compileAndRun(String userCode) {
        // 1. Adding driver code
        String fullCode = userCode + " // Driver main method appended";
        System.out.println("Generated full code with driver.");

        // 2. Performing syntax check
        boolean isValidSyntax = !userCode.contains("SYNTAX_ERROR");
        if (!isValidSyntax) {
            System.out.println("Syntax check failed.");
            return;
        }

        // 3. Running code against test cases
        boolean testsPassed = true;
        System.out.println("Ran test cases: All Passed.");

        // 4. Storing submission in Database
        System.out.println("Saving submission into PostgreSQL database...");

        // 5. Formatting and returning user response
        System.out.println("Response to User: Execution Success [Status: 200 OK]");
    }
}

// ==========================================
// ✅ GOOD DESIGN (Adheres to SRP)
// Broken down into focused classes, each with a single responsibility.
// ==========================================

// Class 1: Responsible ONLY for wrapping user code with driver code.
class DriverCodeGenerator {
    public String generateDriverCode(String userCode) {
        return userCode + " \n// [DriverCodeGenerator] Appended test harness and main()";
    }
}

// Class 2: Responsible ONLY for validating code syntax.
class SyntaxChecker {
    public boolean checkSyntax(String code) {
        return code != null && !code.contains("SYNTAX_ERROR");
    }
}

// Class 3: Responsible ONLY for executing code against test cases.
class TestRunner {
    public boolean runTests(String executableCode) {
        // Simulating test case execution
        return true;
    }
}

// Class 4: Responsible ONLY for persisting execution records/stats.
class DatabaseManager {
    public void saveSubmission(String code, boolean result) {
        System.out.println("[DatabaseManager] Saved execution result to DB: " + (result ? "PASSED" : "FAILED"));
    }
}

// Class 5: Responsible ONLY for formatting output shown to user.
class UserOutputHandler {
    public void displayResult(boolean isSuccess) {
        if (isSuccess) {
            System.out.println("[UserOutputHandler] ✅ All Test Cases Passed! Status: 200 OK");
        } else {
            System.out.println("[UserOutputHandler] ❌ Test Cases Failed / Syntax Error! Status: 400 Bad Request");
        }
    }
}

// Class 6: Coordinator orchestrating the pipeline without owning any low-level logic.
class CompilerCoordinator {
    private final DriverCodeGenerator driverCodeGenerator;
    private final SyntaxChecker syntaxChecker;
    private final TestRunner testRunner;
    private final DatabaseManager databaseManager;
    private final UserOutputHandler userOutputHandler;

    public CompilerCoordinator(
            DriverCodeGenerator driverCodeGenerator,
            SyntaxChecker syntaxChecker,
            TestRunner testRunner,
            DatabaseManager databaseManager,
            UserOutputHandler userOutputHandler) {
        this.driverCodeGenerator = driverCodeGenerator;
        this.syntaxChecker = syntaxChecker;
        this.testRunner = testRunner;
        this.databaseManager = databaseManager;
        this.userOutputHandler = userOutputHandler;
    }

    public void execute(String userCode) {
        // Step 1: Syntax Validation
        if (!syntaxChecker.checkSyntax(userCode)) {
            userOutputHandler.displayResult(false);
            return;
        }

        // Step 2: Driver Code Generation
        String fullCode = driverCodeGenerator.generateDriverCode(userCode);

        // Step 3: Test Execution
        boolean testResult = testRunner.runTests(fullCode);

        // Step 4: Persist to DB
        databaseManager.saveSubmission(fullCode, testResult);

        // Step 5: Deliver Output
        userOutputHandler.displayResult(testResult);
    }
}

public class SRPExample {
    public static void main(String[] args) {
        System.out.println("=== ❌ Running Bad Design (God Class) ===");
        BadTUFCompiler badCompiler = new BadTUFCompiler();
        badCompiler.compileAndRun("int a = 10; int b = 20;");

        System.out.println("\n=== ✅ Running Good Design (SRP Compliant) ===");
        CompilerCoordinator coordinator = new CompilerCoordinator(
                new DriverCodeGenerator(),
                new SyntaxChecker(),
                new TestRunner(),
                new DatabaseManager(),
                new UserOutputHandler()
        );

        coordinator.execute("int a = 10; int b = 20;");
    }
}
