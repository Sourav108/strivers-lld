package service;

import domain.AdminUser;
import domain.CashDrawer;
import domain.Denomination;
import repository.AdminUserRepository;
import repository.CashDrawerRepository;

import java.util.Map;

public class AdminService {
    private final AdminUserRepository adminUserRepository;
    private final CashDrawerRepository cashDrawerRepository;

    public AdminService(AdminUserRepository adminUserRepository, CashDrawerRepository cashDrawerRepository) {
        this.adminUserRepository = adminUserRepository;
        this.cashDrawerRepository = cashDrawerRepository;
    }

    public boolean loginAdmin(String adminId, String pin) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin #" + adminId + " not found."));

        boolean valid = admin.validatePin(pin);
        if (valid) {
            System.out.println("👨‍💼 [Admin Authenticated] " + admin.getName() + " logged in successfully.");
        } else {
            System.out.println("🚫 [Admin Auth Failed] Incorrect PIN for Admin #" + adminId);
        }
        return valid;
    }

    public void refillCash(String atmId, Map<Denomination, Integer> notes) {
        CashDrawer drawer = cashDrawerRepository.findByATMId(atmId)
                .orElseThrow(() -> new IllegalArgumentException("ATM Cash Drawer #" + atmId + " not found."));

        drawer.refill(notes);
        cashDrawerRepository.save(drawer);
        System.out.println("💵 [Cash Refilled] ATM #" + atmId + " refilled with notes: " + notes);
        System.out.println("   New Total ATM Cash: ₹" + drawer.getTotalCashRupees());
    }

    public CashDrawer auditCash(String atmId) {
        CashDrawer drawer = cashDrawerRepository.findByATMId(atmId)
                .orElseThrow(() -> new IllegalArgumentException("ATM Cash Drawer #" + atmId + " not found."));
        System.out.println("🔍 [Audit] " + drawer);
        return drawer;
    }
}
