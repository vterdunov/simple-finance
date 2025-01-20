package org.example;

public interface AuthenticationService {
    void register(String username, String password);
    boolean login(String username, String password);
    void logout();
    User getCurrentUser();
    boolean isAuthenticated();
}