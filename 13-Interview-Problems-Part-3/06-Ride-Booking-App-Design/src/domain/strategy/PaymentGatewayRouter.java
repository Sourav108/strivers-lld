package domain.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentGatewayRouter {
    private final Map<String, PaymentGatewayProvider> providers = new ConcurrentHashMap<>();
    private String defaultProviderName = "MOCK";

    public PaymentGatewayRouter() {
        registerProvider(new MockPaymentGatewayProvider());
    }

    public void registerProvider(PaymentGatewayProvider provider) {
        providers.put(provider.getName().toUpperCase(), provider);
    }

    public PaymentGatewayProvider resolve(String gatewayName) {
        if (gatewayName == null || !providers.containsKey(gatewayName.toUpperCase())) {
            return providers.get(defaultProviderName);
        }
        return providers.get(gatewayName.toUpperCase());
    }

    public void setDefaultProvider(String providerName) {
        this.defaultProviderName = providerName.toUpperCase();
    }
}
