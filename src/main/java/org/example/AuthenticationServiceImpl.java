package org.example;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final InMemoryDataService dataService;
    private User currentUser;

    public AuthenticationServiceImpl(InMemoryDataService dataService) {
        this.dataService = dataService;
        this.currentUser = null;
    }

    @Override
    public void register(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty");
        }
        if (dataService.userExists(username)) {
            throw new AuthenticationException("User already exists");
        }

        User user = new User(username, password);
        dataService.addUser(user);
    }

    @Override
    public boolean login(String username, String password) {
        User user = dataService.getUser(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid username or password");
        }
        currentUser = user;
        return true;
    }

    @Override
    public void logout() {
        currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        if (currentUser == null) {
            throw new AuthenticationException("No user is currently logged in");
        }
        return currentUser;
    }

    @Override
    public boolean isAuthenticated() {
        return currentUser != null;
    }
}