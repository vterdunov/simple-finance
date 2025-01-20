package org.example;

public class FinanceApplication {
    private final ConsoleReader consoleReader;
    private final AuthenticationService authService;
    private final FinancialOperationService financialService;
    private final FileDataService dataService;

    public FinanceApplication() {
        this.dataService = new FileDataService();
        this.authService = new AuthenticationServiceImpl(dataService);
        this.financialService = new FinancialOperationServiceImpl(authService);
        this.consoleReader = new ConsoleReader(authService, financialService, dataService);
    }

    public void run() {
        consoleReader.start();
    }

    public static void main(String[] args) {
        FinanceApplication app = new FinanceApplication();
        app.run();
    }
}