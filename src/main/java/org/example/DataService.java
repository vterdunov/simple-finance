package org.example;

import java.util.Map;

public interface DataService {
    void saveData(Map<String, User> users);
    Map<String, User> loadData();
}