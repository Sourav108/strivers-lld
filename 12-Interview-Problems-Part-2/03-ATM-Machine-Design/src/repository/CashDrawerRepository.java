package repository;

import domain.CashDrawer;
import java.util.Optional;

public interface CashDrawerRepository {
    CashDrawer save(CashDrawer cashDrawer);
    Optional<CashDrawer> findByATMId(String atmId);
}
