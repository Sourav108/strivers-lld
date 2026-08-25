package repository.impl;

import domain.CashDrawer;
import repository.CashDrawerRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CashDrawerRepositoryImpl implements CashDrawerRepository {
    private final Map<String, CashDrawer> drawers = new ConcurrentHashMap<>();

    @Override
    public CashDrawer save(CashDrawer cashDrawer) {
        drawers.put(cashDrawer.getAtmId(), cashDrawer);
        return cashDrawer;
    }

    @Override
    public Optional<CashDrawer> findByATMId(String atmId) {
        return Optional.ofNullable(drawers.get(atmId));
    }
}
