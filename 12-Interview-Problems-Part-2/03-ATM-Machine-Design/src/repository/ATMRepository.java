package repository;

import domain.ATM;
import java.util.List;
import java.util.Optional;

public interface ATMRepository {
    ATM save(ATM atm);
    Optional<ATM> findById(String atmId);
    List<ATM> findAll();
}
