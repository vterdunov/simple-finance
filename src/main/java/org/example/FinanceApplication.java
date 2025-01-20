package org.example;

public class FinanceApplication {
    private final ConsoleReader consoleReader;
    private final AuthenticationService authService;
    private final FinancialOperationService financialService;
    private final InMemoryDataService dataService;

    public FinanceApplication() {
        this.dataService = new InMemoryDataService();
        this.authService = new AuthenticationServiceImpl(dataService);
        this.financialService = new FinancialOperationServiceImpl(authService);
        this.consoleReader = new ConsoleReader(authService, financialService);
    }

    public void run() {
        consoleReader.start();
    }

    public static void main(String[] args) {
        FinanceApplication app = new FinanceApplication();
        app.run();
    }
}