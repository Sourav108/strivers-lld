package repository.impl;

import domain.CancellationPolicy;
import repository.CancellationPolicyRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CancellationPolicyRepositoryImpl implements CancellationPolicyRepository {
    private final Map<String, CancellationPolicy> policies = new ConcurrentHashMap<>();

    @Override
    public CancellationPolicy save(CancellationPolicy policy) {
        policies.put(policy.getId(), policy);
        return policy;
    }

    @Override
    public Optional<CancellationPolicy> findById(String policyId) {
        return Optional.ofNullable(policies.get(policyId));
    }
}
