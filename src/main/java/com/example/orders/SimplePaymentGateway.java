package com.example.orders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SimplePaymentGateway implements PaymentGateway {

    private final String failPrefix;
    private final Map<String, Boolean> customerOutcomes;

    public SimplePaymentGateway(String failPrefix) {
        this(failPrefix, Collections.emptyMap());
    }

    public SimplePaymentGateway(String failPrefix, Map<String, Boolean> customerOutcomes) {
        this.failPrefix = failPrefix == null ? "FAIL" : failPrefix;
        this.customerOutcomes = normalizeOutcomes(customerOutcomes);
    }

    @Override
    public PaymentResult charge(String customerId, BigDecimal amount) {
        if (customerId != null) {
            Boolean predefinedOutcome = customerOutcomes.get(customerId.toUpperCase(Locale.ROOT));
            if (predefinedOutcome != null) {
                return new PaymentResult(predefinedOutcome);
            }
        }

        boolean shouldFail = customerId != null
                && customerId.toUpperCase(Locale.ROOT).startsWith(failPrefix.toUpperCase(Locale.ROOT));
        return new PaymentResult(!shouldFail);
    }

    private Map<String, Boolean> normalizeOutcomes(Map<String, Boolean> outcomes) {
        if (outcomes == null || outcomes.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Boolean> normalized = new HashMap<>();
        for (Map.Entry<String, Boolean> entry : outcomes.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                normalized.put(entry.getKey().toUpperCase(Locale.ROOT), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(normalized);
    }
}
