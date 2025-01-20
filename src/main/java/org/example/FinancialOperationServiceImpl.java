package org.example;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FinancialOperationServiceImpl implements FinancialOperationService {
    private final AuthenticationService authenticationService;

    public FinancialOperationServiceImpl(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void addIncome(BigDecimal amount, String category) {
        validateAmount(amount);
        validateCategory(category);

        User currentUser = getCurrentUser();
        Transaction transaction = new Transaction(amount, category, TransactionType.INCOME);
        currentUser.getWallet().addTransaction(transaction);
    }

    @Override
    public void addExpense(BigDecimal amount, String category) {
        validateAmount(amount);
        validateCategory(category);

        User currentUser = getCurrentUser();
        if (currentUser.getWallet().getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new FinancialOperationException("Insufficient funds");
        }

        Transaction transaction = new Transaction(amount, category, TransactionType.EXPENSE);
        currentUser.getWallet().addTransaction(transaction);

        checkBudgetLimit(category, amount);
    }

    @Override
    public void setBudget(String category, BigDecimal amount) {
        validateAmount(amount);
        validateCategory(category);

        User currentUser = getCurrentUser();
        currentUser.getWallet().setBudget(category, amount);
    }

    @Override
    public Map<String, BigDecimal> getIncomesByCategory() {
        return getCurrentUser().getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)));
    }

    @Override
    public Map<String, BigDecimal> getExpensesByCategory() {
        return getCurrentUser().getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)));
    }

    @Override
    public Map<String, BigDecimal> getBudgetsByCategory() {
        return new HashMap<>(getCurrentUser().getWallet().getBudgets());
    }

    @Override
    public BigDecimal getTotalIncome() {
        return getCurrentUser().getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalExpenses() {
        return getCurrentUser().getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getCurrentBalance() {
        return getCurrentUser().getWallet().getBalance();
    }

    private User getCurrentUser() {
        if (!authenticationService.isAuthenticated()) {
            throw new AuthenticationException("User is not authenticated");
        }
        return authenticationService.getCurrentUser();
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new FinancialOperationException("Amount must be positive");
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new FinancialOperationException("Category cannot be empty");
        }
    }

    private void checkBudgetLimit(String category, BigDecimal amount) {
        User currentUser = getCurrentUser();
        BigDecimal budget = currentUser.getWallet().getBudget(category);
        if (budget.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalExpensesInCategory = getExpensesByCategory()
                    .getOrDefault(category, BigDecimal.ZERO);
            if (totalExpensesInCategory.compareTo(budget) > 0) {
                throw new FinancialOperationException("Budget limit exceeded for category: " + category);
            }
        }
    }
}