package service;

import domain.ATM;
import domain.CashDrawer;
import repository.ATMRepository;
import repository.CashDrawerRepository;

public class ATMService {
    private final ATMRepository atmRepository;
    private final CashDrawerRepository cashDrawerRepository;

    public ATMService(ATMRepository atmRepository, CashDrawerRepository cashDrawerRepository) {
        this.atmRepository = atmRepository;
        this.cashDrawerRepository = cashDrawerRepository;
    }

    public ATM createATM(String id, String location) {
        ATM atm = new ATM(id, location);
        atmRepository.save(atm);
        cashDrawerRepository.save(atm.getCashDrawer());
        System.out.println("🏦 [ATM Initialized] " + atm);
        return atm;
    }

    public ATM getATM(String atmId) {
        return atmRepository.findById(atmId)
                .orElseThrow(() -> new IllegalArgumentException("ATM #" + atmId + " not found."));
    }

    public void takeOffline(String atmId) {
        ATM atm = getATM(atmId);
        atm.setOnline(false);
        atmRepository.save(atm);
        System.out.println("⛔ [ATM Offline] ATM #" + atmId + " taken OUT OF SERVICE.");
    }

    public void bringOnline(String atmId) {
        ATM atm = getATM(atmId);
        atm.setOnline(true);
        atmRepository.save(atm);
        System.out.println("🟢 [ATM Online] ATM #" + atmId + " is now ONLINE (IDLE).");
    }

    public CashDrawer auditCash(String atmId) {
        return getATM(atmId).getCashDrawer();
    }
}
