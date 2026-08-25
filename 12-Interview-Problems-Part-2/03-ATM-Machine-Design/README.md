# ATM Machine System - Low-Level Design

## 1. Problem Statement

Design a secure, state-driven, and robust **Automated Teller Machine (ATM)** system supporting user card insertion, multi-attempt PIN authentication with automatic card blocking, transaction processing (**Cash Withdrawal**, **Cash Deposit**, and **Balance Inquiry**), multi-denomination physical cash dispensing (greedy note allocation), and administrative maintenance (refilling cash, auditing inventory, and taking ATM online/offline).

---

## 2. Requirements

### Functional Requirements
- **Card Insertion & Validation:** User inserts card; validates card status (`isBlocked`).
- **PIN Authentication:** Authenticate PIN; allow a maximum of 3 retries before automatically blocking the card.
- **Transaction Operations:** Once authenticated, user can:
  - Check account balance.
  - Withdraw cash (validated against account balance, daily limit, and ATM cash drawer).
  - Deposit cash notes (calculates total and credits account).
- **Physical Cash Dispensation:** Dispense exact note breakdown across supported denominations (`₹500`, `₹200`, `₹100`) using greedy note allocation.
- **Card Ejection:** Return card upon session completion or user exit.
- **Admin Maintenance:** Refill cash notes, audit inventory, and take ATM in/out of service.

### Important Non-Functional Requirements
- **Hardware State Integrity:** Enforce valid operational sequences via the **State Pattern** (`IDLE` $\rightarrow$ `CARD_INSERTED` $\rightarrow$ `AUTHENTICATED` $\rightarrow$ `TRANSACTION_SELECTED` $\rightarrow$ `TRANSACTION_COMPLETED` $\rightarrow$ `IDLE`).
- **Data Consistency & Floating Point Safety:** All financial amounts are stored in **minor units** (paisa/cents as `long`) to prevent precision loss.
- **Thread Safety:** Synchronized account balance mutations and cash drawer modifications.

---

## 3. Package Structure

```
src/
├── controller/
│   ├── AdminController.java
│   ├── ATMController.java
│   ├── CardController.java
│   ├── SessionController.java
│   └── TransactionController.java
├── domain/
│   ├── exception/
│   │   ├── CardBlockedException.java
│   │   ├── InsufficientFundsException.java
│   │   └── InvalidATMOperationException.java
│   ├── state/
│   │   ├── ATMState.java             (State Interface)
│   │   ├── AuthenticatedState.java
│   │   ├── CardInsertedState.java
│   │   ├── IdleState.java
│   │   ├── OutOfServiceState.java
│   │   ├── TransactionCompletedState.java
│   │   └── TransactionSelectedState.java
│   ├── strategy/
│   │   ├── BalanceInquiryStrategy.java
│   │   ├── DepositStrategy.java
│   │   ├── TransactionStrategy.java  (Strategy Interface)
│   │   └── WithdrawalStrategy.java
│   ├── Account.java
│   ├── AdminUser.java
│   ├── ATM.java                      (State Context & Hardware Root)
│   ├── Card.java
│   ├── CashDrawer.java
│   ├── Denomination.java             (Enum: 500, 200, 100)
│   ├── Session.java
│   ├── Transaction.java
│   ├── TransactionStatus.java        (Enum: PENDING, SUCCESS, FAILED)
│   └── TransactionType.java          (Enum: WITHDRAW, DEPOSIT, BALANCE)
├── repository/
│   ├── impl/
│   │   ├── AccountRepositoryImpl.java
│   │   ├── AdminUserRepositoryImpl.java
│   │   ├── ATMRepositoryImpl.java
│   │   ├── CardRepositoryImpl.java
│   │   ├── CashDrawerRepositoryImpl.java
│   │   ├── SessionRepositoryImpl.java
│   │   └── TransactionRepositoryImpl.java
│   ├── AccountRepository.java        (Interface)
│   ├── AdminUserRepository.java      (Interface)
│   ├── ATMRepository.java            (Interface)
│   ├── CardRepository.java           (Interface)
│   ├── CashDrawerRepository.java     (Interface)
│   ├── SessionRepository.java        (Interface)
│   └── TransactionRepository.java    (Interface)
├── service/
│   ├── AdminService.java
│   ├── ATMService.java
│   ├── CardService.java
│   ├── SessionService.java
│   └── TransactionService.java
└── main/
    └── ATMSimulation.java            (Driver Simulation)
```

---

## 4. Class Responsibilities

| Package | Class / Interface | Responsibility (1 Line) |
|---|---|---|
| `domain` | **`ATM`** | Context managing current hardware state, active session, and cash drawer. |
| `domain` | **`Account`** | Bank account managing balance and daily withdrawal limits in minor units. |
| `domain` | **`Card`** | Payment card managing PIN verification, retries counter, and blocked status. |
| `domain` | **`CashDrawer`** | Physical cash vault managing note inventory and greedy dispensing algorithm. |
| `domain` | **`Session`** | Tracks active user interaction session on an ATM. |
| `domain` | **`Transaction`** | Record capturing transaction type, amount, note breakdowns, and status. |
| `domain.state` | **`ATMState`** | State pattern interface defining allowable ATM hardware actions. |
| `domain.state` | **`IdleState`**, **`CardInsertedState`**, etc. | Concrete hardware states enforcing legal operational workflows. |
| `domain.strategy`| **`TransactionStrategy`** | Strategy interface defining transaction execution logic. |
| `domain.strategy`| **`WithdrawalStrategy`**, **`DepositStrategy`**, etc. | Concrete transaction processors handling balance & drawer updates. |
| `repository` | **`ATMRepository`**, **`AccountRepository`**, etc. | Persistence contracts for ATM entities. |
| `service` | **`TransactionService`** | Coordinates execution of withdrawal, deposit, and balance strategies. |
| `service` | **`CardService`** | Validates and authenticates cards against PIN retry limits. |
| `service` | **`SessionService`** | Manages user session creation and teardown. |
| `service` | **`AdminService`** | Handles admin authentication, cash refills, and drawer audits. |
| `service` | **`ATMService`** | Manages ATM lifecycle and online/offline states. |
| `controller` | **`*Controller`** | Entrypoint controllers exposing clean API endpoints. |
| `main` | **`ATMSimulation`** | Executable simulation driver testing all ATM use cases and edge cases. |

---

## 5. Design Patterns & SOLID Principles

- **State Pattern:**
  - Manages ATM hardware lifecycle (`IdleState` $\rightarrow$ `CardInsertedState` $\rightarrow$ `AuthenticatedState` $\rightarrow$ `TransactionSelectedState` $\rightarrow$ `TransactionCompletedState` $\rightarrow$ `IdleState`).
  - Blocks invalid actions (e.g. withdrawing cash in `IDLE` state) by throwing `InvalidATMOperationException`.
- **Strategy Pattern:**
  - `TransactionStrategy` decouples transaction algorithms (`WithdrawalStrategy`, `DepositStrategy`, `BalanceInquiryStrategy`) from `TransactionService`.
- **Single Responsibility Principle (SRP):**
  - `CashDrawer` only manages note dispensing and denominations; `Account` only manages bank balances and daily limits; `Card` only manages PIN retries.
- **Open/Closed Principle (OCP):**
  - New transaction types (e.g. `FundTransferStrategy`, `PinChangeStrategy`) can be added by implementing `TransactionStrategy` without modifying existing state or service classes.

---

## 6. Main Flows

### Flow 1: Cash Withdrawal (Greedy Note Dispensation)
```
TransactionController.withdrawCash(sessionId, ₹2,800)
  -> TransactionService retrieves ATM and Account
  -> ATM changes state: AUTHENTICATED -> TRANSACTION_SELECTED(WITHDRAW)
  -> WithdrawalStrategy.execute():
     1. Account.withdraw(₹2,800) -> checks balance (₹50,000) & daily limit (₹20,000)
     2. CashDrawer.calculateAndDispense(₹2,800):
        -> 5 x ₹500 = ₹2,500
        -> 1 x ₹200 = ₹200
        -> 1 x ₹100 = ₹100
     3. Updates notes inventory in CashDrawer
     4. Sets Transaction status to SUCCESS
  -> ATM changes state: TRANSACTION_SELECTED -> TRANSACTION_COMPLETED
```

### Flow 2: PIN Authentication & Security Guard
```
CardController.authenticateCard(atm, cardId, pin: "1234")
  -> Card.authenticate(pin):
     -> If correct: resets pinRetriesLeft = 3, returns TRUE
        -> ATM changes state: CARD_INSERTED -> AUTHENTICATED
     -> If incorrect: decrements pinRetriesLeft
        -> If pinRetriesLeft <= 0: Card.isBlocked = true, throws CardBlockedException
```

---

## 7. Edge Cases Handled

1. **3 Failed PIN Attempts:** Card is immediately blocked (`isBlocked = true`) throwing `CardBlockedException`.
2. **Insufficient Account Balance:** Checked before drawer note deduction, throwing `InsufficientFundsException`.
3. **Daily Limit Exceeded:** Validates cumulative daily withdrawals against `dailyWithdrawalLimitMinor`.
4. **Insufficient ATM Cash or Denomination Mismatch:** If the drawer lacks exact change (e.g. only ₹500 notes available for a ₹200 request), transaction fails gracefully without deducting money from the account.
5. **Out of Service Guard:** When ATM is offline (`OutOfServiceState`), all card insertions and operations are rejected with `InvalidATMOperationException`.

---

## 8. How to Run

Compile and execute from the `03-ATM-Machine-Design` directory:

```bash
# Compile all packaged Java sources
javac -d bin $(find src -name "*.java")

# Run the complete demonstration driver
java -cp bin main.ATMSimulation
```

---

## 9. Interview Thinking

### How I Would Explain This in an Interview
1. **Step 1 (Clarify Requirements):** Focus on card insert $\rightarrow$ PIN auth $\rightarrow$ transaction options $\rightarrow$ cash dispensing $\rightarrow$ card eject $\rightarrow$ admin refill.
2. **Step 2 (Identify Core Entities):** `ATM`, `Account`, `Card`, `CashDrawer`, `Session`, `Transaction`.
3. **Step 3 (Select Design Patterns):**
   - **State Pattern** for ATM hardware transitions.
   - **Strategy Pattern** for transaction behaviors.
4. **Step 4 (Explain Note Dispensing):** Walk through the greedy denomination algorithm (`₹500` $\rightarrow$ `₹200` $\rightarrow$ `₹100`) and transactional rollback on failure.

### Likely Interviewer Follow-up Questions
1. *How would you handle power failure during cash dispensing?*
   - **Answer:** Use 2-Phase Commit (2PC) or SAGA pattern with physical sensor hardware acknowledgment. If sensor fails to confirm note ejection, the ledger transaction is automatically rolled back.
2. *Why store money as `long` instead of `double`?*
   - **Answer:** Floating-point arithmetic (`double`/`float`) causes binary rounding errors (e.g., `0.1 + 0.2 = 0.30000000000000004`). Minor units (`long` cents/paisa) guarantee exact integer arithmetic.

---

## 🎯 Quick Summary

- **Problem:** Design a secure ATM system with card authentication, transaction processing, greedy cash dispensing, and admin controls.
- **Core Classes:** `ATM`, `ATMState` (`IdleState`, `AuthenticatedState`, etc.), `CashDrawer`, `Account`, `Card`, `TransactionStrategy` (`WithdrawalStrategy`, etc.).
- **Main Flow:** Insert Card $\rightarrow$ Enter PIN $\rightarrow$ Select Transaction $\rightarrow$ Execute Strategy (Dispense Notes & Deduct Balance) $\rightarrow$ Eject Card.
- **Important Design:** State Pattern for hardware lifecycle; Strategy Pattern for transactions; Greedy algorithm for note allocation.
- **Edge Cases:** 3 PIN retry card block, daily limit check, insufficient drawer cash, denomination mismatch, and out-of-service lockout.
- **LLD Takeaway:** Combine State Pattern for machine flow and Strategy Pattern for transaction execution to create a clean, maintainable ATM architecture.
- **Memorable Rule:** *"State controls the machine, Strategy executes the transaction, and Minor units protect the money."*
