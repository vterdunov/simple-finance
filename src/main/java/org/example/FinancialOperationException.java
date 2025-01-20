package org.example;

public class FinancialOperationException extends RuntimeException {
    public FinancialOperationException(String message) {
        super(message);
    }
}