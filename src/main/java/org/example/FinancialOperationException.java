package org.example;

// Исключение для обработки ошибок финансовых операций
public class FinancialOperationException extends RuntimeException {

    public FinancialOperationException(String message) {
        super(message);
    }
}
