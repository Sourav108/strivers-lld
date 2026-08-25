package domain;

import domain.state.ATMState;
import domain.state.IdleState;
import domain.state.OutOfServiceState;

import java.util.Map;

public class ATM {
    private final String id;
    private final String location;
    private boolean isOnline;
    private ATMState currentState;
    private final CashDrawer cashDrawer;
    private Session currentSession;
    private String insertedCardId;
    private TransactionType selectedTransactionType;

    public ATM(String id, String location) {
        this.id = id;
        this.location = location;
        this.isOnline = true;
        this.cashDrawer = new CashDrawer(id);
        this.currentState = new IdleState();
    }

    public String getId() { return id; }
    public String getLocation() { return location; }
    public boolean isOnline() { return isOnline; }
    public ATMState getCurrentState() { return currentState; }
    public CashDrawer getCashDrawer() { return cashDrawer; }
    public Session getCurrentSession() { return currentSession; }
    public String getInsertedCardId() { return insertedCardId; }
    public TransactionType getSelectedTransactionType() { return selectedTransactionType; }

    public void setOnline(boolean online) {
        this.isOnline = online;
        if (!online) {
            this.currentState = new OutOfServiceState();
        } else if (this.currentState instanceof OutOfServiceState) {
            this.currentState = new IdleState();
        }
    }

    public void setState(ATMState state) {
        this.currentState = state;
    }

    public void setCurrentSession(Session session) {
        this.currentSession = session;
    }

    public void setInsertedCardId(String cardId) {
        this.insertedCardId = cardId;
    }

    public void setSelectedTransactionType(TransactionType type) {
        this.selectedTransactionType = type;
    }

    // State pattern delegates
    public void insertCard(String cardId) {
        currentState.insertCard(this, cardId);
    }

    public void enterPin(String pin) {
        currentState.enterPin(this, pin);
    }

    public void selectTransaction(TransactionType type) {
        currentState.selectTransaction(this, type);
    }

    public void processWithdrawal(long amountRupees) {
        currentState.processWithdrawal(this, amountRupees);
    }

    public void processDeposit(Map<Denomination, Integer> notes) {
        currentState.processDeposit(this, notes);
    }

    public long checkBalance() {
        return currentState.checkBalance(this);
    }

    public void ejectCard() {
        currentState.ejectCard(this);
    }

    @Override
    public String toString() {
        return "ATM[" + id + " | " + location + " | State: " + currentState.getStateName() + " | Cash: ₹" + cashDrawer.getTotalCashRupees() + "]";
    }
}
