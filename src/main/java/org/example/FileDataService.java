package org.example;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FileDataService implements DataService {
    private static final String FILE_PATH = "users.json";
    private final Gson gson;
    private final Map<String, User> users;

    public FileDataService() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        this.users = loadData();
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }

    @Override
    public void saveData(Map<String, User> users) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    @Override
    public Map<String, User> loadData() {
        try {
            if (!Files.exists(Paths.get(FILE_PATH))) {
                // Если файл не существует, создаем новый с пустым объектом
                Map<String, User> emptyMap = new HashMap<>();
                saveData(emptyMap);
                return emptyMap;
            }

            String content = Files.readString(Paths.get(FILE_PATH)).trim();
            if (content.isEmpty()) {
                // Если файл пустой, инициализируем его пустым объектом
                Map<String, User> emptyMap = new HashMap<>();
                saveData(emptyMap);
                return emptyMap;
            }

            try (Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
                Type type = new TypeToken<Map<String, User>>(){}.getType();
                Map<String, User> loadedUsers = gson.fromJson(reader, type);
                return loadedUsers != null ? loadedUsers : new HashMap<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            return new HashMap<>();
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing JSON data: " + e.getMessage());
            // В случае ошибки парсинга, создаем новый файл с пустым объектом
            Map<String, User> emptyMap = new HashMap<>();
            saveData(emptyMap);
            return emptyMap;
        }
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
        saveData(users);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public void removeUser(String username) {
        users.remove(username);
        saveData(users);
    }
}