package org.example;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Модель тразакции (доход или расход)
public class Transaction {

    private final BigDecimal amount;
    private final String category;
    private final LocalDateTime dateTime;
    private final TransactionType type;

    public Transaction(
        BigDecimal amount,
        String category,
        TransactionType type
    ) {
        this.amount = amount;
        this.category = category;
        this.dateTime = LocalDateTime.now();
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public TransactionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format(
            "%s - %s: %s %.2f at %s",
            type.getDisplayName(),
            category,
            amount.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "-",
            amount.abs(),
            dateTime
        );
    }
}
