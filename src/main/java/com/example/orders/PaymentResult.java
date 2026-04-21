package com.example.orders;

public class PaymentResult {
    private final boolean successful;

    public PaymentResult(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
