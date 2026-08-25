package repository.impl;

import domain.ATM;
import repository.ATMRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ATMRepositoryImpl implements ATMRepository {
    private final Map<String, ATM> atms = new ConcurrentHashMap<>();

    @Override
    public ATM save(ATM atm) {
        atms.put(atm.getId(), atm);
        return atm;
    }

    @Override
    public Optional<ATM> findById(String atmId) {
        return Optional.ofNullable(atms.get(atmId));
    }

    @Override
    public List<ATM> findAll() {
        return new ArrayList<>(atms.values());
    }
}
