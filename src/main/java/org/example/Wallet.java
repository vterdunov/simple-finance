package org.example;

import java.math.BigDecimal;
import java.util.*;

// Модель кошелька пользователя
public class Wallet {

    private BigDecimal balance;
    private final List<Transaction> transactions;
    private final Map<String, BigDecimal> budgets;

    public Wallet() {
        this.balance = BigDecimal.ZERO;
        this.transactions = new ArrayList<>();
        this.budgets = new HashMap<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        if (transaction.getType() == TransactionType.INCOME) {
            balance = balance.add(transaction.getAmount());
        } else {
            balance = balance.subtract(transaction.getAmount());
        }
    }

    public void setBudget(String category, BigDecimal amount) {
        budgets.put(category, amount);
    }

    public BigDecimal getBudget(String category) {
        return budgets.getOrDefault(category, BigDecimal.ZERO);
    }

    // Getters
    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public Map<String, BigDecimal> getBudgets() {
        return Collections.unmodifiableMap(budgets);
    }
}
