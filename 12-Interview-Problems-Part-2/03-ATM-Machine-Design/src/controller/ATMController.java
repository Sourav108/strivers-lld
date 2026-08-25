package controller;

import domain.ATM;
import domain.CashDrawer;
import service.ATMService;

public class ATMController {
    private final ATMService atmService;

    public ATMController(ATMService atmService) {
        this.atmService = atmService;
    }

    public ATM createATM(String id, String location) {
        return atmService.createATM(id, location);
    }

    public void takeOffline(String atmId) {
        atmService.takeOffline(atmId);
    }

    public void bringOnline(String atmId) {
        atmService.bringOnline(atmId);
    }

    public CashDrawer auditCash(String atmId) {
        return atmService.auditCash(atmId);
    }
}
