package org.example;

import java.math.BigDecimal;
import java.util.Map;

public interface FinancialOperationService {
    void addIncome(BigDecimal amount, String category);
    void addExpense(BigDecimal amount, String category);
    void setBudget(String category, BigDecimal amount);
    Map<String, BigDecimal> getIncomesByCategory();
    Map<String, BigDecimal> getExpensesByCategory();
    Map<String, BigDecimal> getBudgetsByCategory();
    BigDecimal getTotalIncome();
    BigDecimal getTotalExpenses();
    BigDecimal getCurrentBalance();
}