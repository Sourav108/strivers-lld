import java.util.ArrayList;
import java.util.List;

/**
 * UML Class Diagrams: Complete Relationship Demonstration in Java
 * 
 * Demonstrates:
 * 1. Visibility Modifiers (+, -, #, ~)
 * 2. Special Classifiers (<<interface>>, <<abstract>>, <<enumeration>>)
 * 3. The 6 Core UML Relationships:
 *    - Inheritance (IS-A)
 *    - Realization (IMPLEMENTS)
 *    - Association (USE-A / Linked)
 *    - Aggregation (Weak HAS-A: independent lifecycles)
 *    - Composition (Strong HAS-A: coupled lifecycles)
 *    - Dependency (USES-TEMPORARILY: method-level transient parameter)
 */

// =========================================================================
// 1. Visibility Modifiers & Attribute/Method Syntax
// =========================================================================
class Person {
    // Visibility Markers
    public String name;             // + name: String
    private int age;                // - age: int
    protected String nationalId;    // # nationalId: String
    String city;                    // ~ city: String (package-private)

    public Person(String name, int age, String nationalId, String city) {
        this.name = name;
        this.age = age;
        this.nationalId = nationalId;
        this.city = city;
    }

    // - isAdult(age: int): boolean
    private boolean isAdult(int checkAge) {
        return checkAge >= 18;
    }

    // + canVote(): boolean
    public boolean canVote() {
        return isAdult(this.age);
    }
}

// =========================================================================
// 2. Special Classifiers: Enum, Interface, Abstract Class
// =========================================================================

// <<enumeration>>
enum OrderStatus {
    PENDING, PROCESSING, COMPLETED, CANCELLED
}

// <<interface>>
interface Payable {
    double calculatePay(); // + calculatePay(): double
}

// <<abstract>>
abstract class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public abstract void makeSound(); // + makeSound(): void
}

// =========================================================================
// 3. The 6 UML Relationships
// =========================================================================

// --- [Relationship 1: Inheritance (IS-A)] ---
// Dog IS-A Animal (Solid line with hollow triangle: Animal <|-- Dog)
class Dog extends Animal {
    public Dog(String name) {
        super(name);
    }

    @Override
    public void makeSound() {
        System.out.println("🐶 [Inheritance] " + name + " says: Woof Woof!");
    }
}

// --- [Relationship 2: Realization (IMPLEMENTS)] ---
// FullTimeEmployee REALIZES Payable (Dashed line with hollow triangle: Payable <|.. FullTimeEmployee)
class FullTimeEmployee implements Payable {
    private final String empId;
    private final double monthlySalary;

    public FullTimeEmployee(String empId, double monthlySalary) {
        this.empId = empId;
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculatePay() {
        return monthlySalary;
    }

    public String getEmpId() { return empId; }
}

// --- [Relationship 3: Association (USE-A / Structural Link)] ---
// Student and Teacher know each other (Solid line: Teacher --> Student)
class Student {
    private final String name;
    public Student(String name) { this.name = name; }
    public String getName() { return name; }
}

class Teacher {
    private final String name;
    private final List<Student> students = new ArrayList<>();

    public Teacher(String name) { this.name = name; }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void displayStudents() {
        System.out.print("👨‍🏫 [Association] Teacher " + name + " teaches: ");
        for (Student s : students) System.out.print(s.getName() + " ");
        System.out.println();
    }
}

// --- [Relationship 4: Aggregation (Weak HAS-A)] ---
// Department HAS Professors. If Department is deleted, Professors STILL exist!
// (Hollow diamond: Department o-- Professor)
class Professor {
    private final String name;
    public Professor(String name) { this.name = name; }
    public String getName() { return name; }
}

class Department {
    private final String deptName;
    private final List<Professor> professors; // Aggregated (passed from outside)

    public Department(String deptName, List<Professor> professors) {
        this.deptName = deptName;
        this.professors = professors;
    }

    public void printDepartment() {
        System.out.println("🏛️ [Aggregation] Dept: " + deptName + " with " + professors.size() + " professors (Professors outlive Dept).");
    }
}

// --- [Relationship 5: Composition (Strong HAS-A)] ---
// House HAS Rooms. Rooms are created and managed by House; if House is destroyed, Rooms die too!
// (Filled diamond: House *-- Room)
class Room {
    private final String type;
    public Room(String type) { this.type = type; }
    public String getType() { return type; }
}

class House {
    private final List<Room> rooms = new ArrayList<>(); // Composed (created internally)

    public House() {
        // Lifecycle bound to House
        rooms.add(new Room("Master Bedroom"));
        rooms.add(new Room("Kitchen"));
        rooms.add(new Room("Living Room"));
    }

    public void describeHouse() {
        System.out.println("🏠 [Composition] House has " + rooms.size() + " rooms (Rooms cannot exist without House).");
    }
}

// --- [Relationship 6: Dependency (USES-TEMPORARILY)] ---
// OrderService depends on PaymentGateway ONLY during method execution.
// (Dashed line with open arrow: OrderService ..> PaymentGateway)
class PaymentGateway {
    public boolean processPayment(double amount) {
        System.out.println("💳 [Dependency] PaymentGateway processed $" + amount + " successfully.");
        return true;
    }
}

class OrderService {
    // Dependency injected as a method parameter, NOT held as long-term state
    public void checkout(PaymentGateway paymentGateway, double amount) {
        System.out.println("📦 [Dependency] OrderService checking out order of $" + amount + "...");
        paymentGateway.processPayment(amount);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class ClassDiagramExample {
    public static void main(String[] args) {
        System.out.println("=== 1. Visibility & Class Attributes ===");
        Person person = new Person("Sourav", 21, "ID-9876", "Bangalore");
        System.out.println("Name: " + person.name + ", Can Vote: " + person.canVote());

        System.out.println("\n=== 2. Inheritance (IS-A) ===");
        Animal dog = new Dog("Buddy");
        dog.makeSound();

        System.out.println("\n=== 3. Realization (IMPLEMENTS) ===");
        Payable employee = new FullTimeEmployee("EMP-101", 8500.0);
        System.out.println("💼 [Realization] Calculated Pay: $" + employee.calculatePay());

        System.out.println("\n=== 4. Association (USE-A) ===");
        Teacher teacher = new Teacher("Dr. Sharma");
        teacher.addStudent(new Student("Alice"));
        teacher.addStudent(new Student("Bob"));
        teacher.displayStudents();

        System.out.println("\n=== 5. Aggregation (Weak HAS-A) ===");
        List<Professor> profList = new ArrayList<>();
        profList.add(new Professor("Dr. Alan"));
        profList.add(new Professor("Dr. Ada"));
        Department csDept = new Department("Computer Science", profList);
        csDept.printDepartment();

        System.out.println("\n=== 6. Composition (Strong HAS-A) ===");
        House house = new House();
        house.describeHouse();

        System.out.println("\n=== 7. Dependency (USES-TEMPORARILY) ===");
        OrderService orderService = new OrderService();
        PaymentGateway gateway = new PaymentGateway();
        orderService.checkout(gateway, 299.99);
    }
}
