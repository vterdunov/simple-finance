package org.example;

import java.util.Map;

public interface DataService {
    void saveData(Map<String, User> users);
    Map<String, User> loadData();
    void addUser(User user);
    User getUser(String username);
    boolean userExists(String username);
    void removeUser(String username);
}