package org.example;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;

public class ConsoleReader {
    private final Scanner scanner;
    private final AuthenticationService authService;
    private final FinancialOperationService financialService;
    private final DataService dataService;

    public ConsoleReader(AuthenticationService authService, FinancialOperationService financialService, DataService dataService) {
        this.scanner = new Scanner(System.in);
        this.authService = authService;
        this.financialService = financialService;
        this.dataService = dataService;
    }

    public void start() {
        boolean running = true;
        while (running) {
            if (!authService.isAuthenticated()) {
                running = handleAuthenticationMenu();
            } else {
                running = handleMainMenu();
            }
        }
        saveAndExit();
    }

    private void saveAndExit() {
        if (authService.getCurrentUser() != null) {
            dataService.saveData(Map.of(authService.getCurrentUser().getUsername(),
                    authService.getCurrentUser()));
        }
        scanner.close();
    }

    private boolean handleAuthenticationMenu() {
        System.out.println("\n=== Authentication Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> handleLogin();
            case "2" -> handleRegistration();
            case "3" -> {
                return false;
            }
            default -> System.out.println("Invalid option");
        }
        return true;
    }

    private boolean handleMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Add Income");
        System.out.println("2. Add Expense");
        System.out.println("3. Set Budget");
        System.out.println("4. View Balance");
        System.out.println("5. View Statistics");
        System.out.println("6. Logout");
        System.out.println("7. Exit");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> handleAddIncome();
            case "2" -> handleAddExpense();
            case "3" -> handleSetBudget();
            case "4" -> handleViewBalance();
            case "5" -> handleViewStatistics();
            case "6" -> {
                authService.logout();
                System.out.println("Logged out successfully");
            }
            case "7" -> {
                return false;
            }
            default -> System.out.println("Invalid option");
        }
        return true;
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            authService.login(username, password);
            System.out.println("Login successful!");
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void handleRegistration() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            authService.register(username, password);
            System.out.println("Registration successful!");
        } catch (AuthenticationException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void handleAddIncome() {
        try {
            System.out.print("Enter amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            System.out.print("Enter category: ");
            String category = scanner.nextLine();

            financialService.addIncome(amount, category);
            System.out.println("Income added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format");
        } catch (FinancialOperationException e) {
            System.out.println("Failed to add income: " + e.getMessage());
        }
    }

    private void handleAddExpense() {
        try {
            System.out.print("Enter amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            System.out.print("Enter category: ");
            String category = scanner.nextLine();

            financialService.addExpense(amount, category);
            System.out.println("Expense added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format");
        } catch (FinancialOperationException e) {
            System.out.println("Failed to add expense: " + e.getMessage());
        }
    }

    private void handleSetBudget() {
        try {
            System.out.print("Enter category: ");
            String category = scanner.nextLine();
            System.out.print("Enter budget amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            financialService.setBudget(category, amount);
            System.out.println("Budget set successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format");
        } catch (FinancialOperationException e) {
            System.out.println("Failed to set budget: " + e.getMessage());
        }
    }

    private void handleViewBalance() {
        System.out.println("\n=== Balance ===");
        System.out.println("Current balance: " + financialService.getCurrentBalance());
        System.out.println("Total income: " + financialService.getTotalIncome());
        System.out.println("Total expenses: " + financialService.getTotalExpenses());
    }

    private void handleViewStatistics() {
        System.out.println("\n=== Statistics ===");
        System.out.println("Income by category:");
        financialService.getIncomesByCategory()
                .forEach((category, amount) -> System.out.println(category + ": " + amount));

        System.out.println("\nExpenses by category:");
        financialService.getExpensesByCategory()
                .forEach((category, amount) -> System.out.println(category + ": " + amount));

        System.out.println("\nBudgets by category:");
        financialService.getBudgetsByCategory()
                .forEach((category, budget) -> {
                    BigDecimal spent = financialService.getExpensesByCategory().getOrDefault(category, BigDecimal.ZERO);
                    BigDecimal remaining = budget.subtract(spent);
                    System.out.println(category + ": Budget=" + budget + ", Remaining=" + remaining);
                });
    }
}