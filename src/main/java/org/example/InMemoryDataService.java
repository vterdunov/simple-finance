package org.example;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDataService implements DataService {
    private final Map<String, User> users;

    public InMemoryDataService() {
        this.users = new HashMap<>();
    }

    @Override
    public void saveData(Map<String, User> users) {
        this.users.clear();
        this.users.putAll(users);
    }

    @Override
    public Map<String, User> loadData() {
        return new HashMap<>(users);
    }

    // Дополнительные методы для прямой работы с данными
    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }
}