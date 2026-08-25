package domain;

import domain.exception.InsufficientFundsException;

import java.util.EnumMap;
import java.util.Map;

public class CashDrawer {
    private final String atmId;
    private final Map<Denomination, Integer> notesByDenomination = new EnumMap<>(Denomination.class);

    public CashDrawer(String atmId) {
        this.atmId = atmId;
        for (Denomination d : Denomination.values()) {
            notesByDenomination.put(d, 0);
        }
    }

    public String getAtmId() { return atmId; }

    public synchronized void refill(Map<Denomination, Integer> notes) {
        for (Map.Entry<Denomination, Integer> entry : notes.entrySet()) {
            notesByDenomination.put(entry.getKey(),
                    notesByDenomination.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }

    public synchronized void deposit(Map<Denomination, Integer> notes) {
        refill(notes);
    }

    public synchronized long getTotalCashMinor() {
        long totalRupees = 0;
        for (Map.Entry<Denomination, Integer> entry : notesByDenomination.entrySet()) {
            totalRupees += (long) entry.getKey().getValue() * entry.getValue();
        }
        return totalRupees * 100; // in minor units (paisa)
    }

    public synchronized double getTotalCashRupees() {
        return getTotalCashMinor() / 100.0;
    }

    public synchronized Map<Denomination, Integer> calculateAndDispense(long amountRupees) {
        if (amountRupees <= 0 || amountRupees % 100 != 0) {
            throw new IllegalArgumentException("Withdrawal amount must be a multiple of ₹100.");
        }

        if (amountRupees * 100 > getTotalCashMinor()) {
            throw new InsufficientFundsException("❌ ATM cash drawer does not have sufficient physical cash. Available: ₹" + getTotalCashRupees());
        }

        Map<Denomination, Integer> dispensed = new EnumMap<>(Denomination.class);
        long remaining = amountRupees;

        // Greedy algorithm to dispense largest denominations first
        Denomination[] ordered = {Denomination.FIVE_HUNDRED, Denomination.TWO_HUNDRED, Denomination.ONE_HUNDRED};
        for (Denomination d : ordered) {
            int availableNotes = notesByDenomination.getOrDefault(d, 0);
            int neededNotes = (int) (remaining / d.getValue());
            int takeNotes = Math.min(availableNotes, neededNotes);

            if (takeNotes > 0) {
                dispensed.put(d, takeNotes);
                remaining -= (long) takeNotes * d.getValue();
            }
        }

        if (remaining > 0) {
            throw new InsufficientFundsException("❌ ATM cannot dispense exact change for ₹" + amountRupees + " with current note denominations.");
        }

        // Deduct notes from drawer
        for (Map.Entry<Denomination, Integer> entry : dispensed.entrySet()) {
            notesByDenomination.put(entry.getKey(), notesByDenomination.get(entry.getKey()) - entry.getValue());
        }

        return dispensed;
    }

    public synchronized Map<Denomination, Integer> getInventory() {
        return new EnumMap<>(notesByDenomination);
    }

    @Override
    public String toString() {
        return "CashDrawer[ATM: " + atmId + " | Total: ₹" + getTotalCashRupees() + " | Breakdown: " + notesByDenomination + "]";
    }
}
