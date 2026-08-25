package repository;

import domain.CancellationPolicy;
import java.util.Optional;

public interface CancellationPolicyRepository {
    CancellationPolicy save(CancellationPolicy policy);
    Optional<CancellationPolicy> findById(String policyId);
}
