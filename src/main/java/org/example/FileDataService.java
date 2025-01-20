package org.example;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Реализация сервиса данных с хранением в JSON файле
public class FileDataService implements DataService {

    private static final String FILE_PATH = "users.json";
    private final Gson gson;
    private final Map<String, User> users;

    public FileDataService() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(
                LocalDateTime.class,
                new LocalDateTimeAdapter()
            )
            .registerTypeAdapter(Transaction.class, new TransactionAdapter())
            .registerTypeAdapter(BigDecimal.class, new BigDecimalAdapter())
            .registerTypeAdapter(Wallet.class, new WalletAdapter())
            .create();
        this.users = loadData();
    }

    // Сериализации
    private static class LocalDateTimeAdapter
        implements
            JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        private static final DateTimeFormatter formatter =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(
            LocalDateTime src,
            Type typeOfSrc,
            JsonSerializationContext context
        ) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDateTime deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
        ) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }

    private static class BigDecimalAdapter
        implements JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {

        @Override
        public JsonElement serialize(
            BigDecimal src,
            Type typeOfSrc,
            JsonSerializationContext context
        ) {
            return new JsonPrimitive(src);
        }

        @Override
        public BigDecimal deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
        ) throws JsonParseException {
            return new BigDecimal(json.getAsString());
        }
    }

    private static class TransactionAdapter
        implements JsonSerializer<Transaction>, JsonDeserializer<Transaction> {

        @Override
        public JsonElement serialize(
            Transaction src,
            Type typeOfSrc,
            JsonSerializationContext context
        ) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("amount", context.serialize(src.getAmount()));
            jsonObject.addProperty("category", src.getCategory());
            jsonObject.add("dateTime", context.serialize(src.getDateTime()));
            jsonObject.addProperty("type", src.getType().name());
            return jsonObject;
        }

        @Override
        public Transaction deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
        ) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            BigDecimal amount = context.deserialize(
                jsonObject.get("amount"),
                BigDecimal.class
            );
            String category = jsonObject.get("category").getAsString();
            TransactionType type = TransactionType.valueOf(
                jsonObject.get("type").getAsString()
            );

            return new Transaction(amount, category, type);
        }
    }

    private static class WalletAdapter
        implements JsonSerializer<Wallet>, JsonDeserializer<Wallet> {

        @Override
        public JsonElement serialize(
            Wallet src,
            Type typeOfSrc,
            JsonSerializationContext context
        ) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("balance", context.serialize(src.getBalance()));
            jsonObject.add(
                "transactions",
                context.serialize(src.getTransactions())
            );
            jsonObject.add("budgets", context.serialize(src.getBudgets()));
            return jsonObject;
        }

        @Override
        public Wallet deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
        ) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            Wallet wallet = new Wallet();

            // Восстанавливаем транзакции
            Type transactionListType = new TypeToken<
                List<Transaction>
            >() {}.getType();
            List<Transaction> transactions = context.deserialize(
                jsonObject.get("transactions"),
                transactionListType
            );
            if (transactions != null) {
                for (Transaction transaction : transactions) {
                    wallet.addTransaction(transaction);
                }
            }

            // Восстанавливаем бюджеты
            Type budgetMapType = new TypeToken<
                Map<String, BigDecimal>
            >() {}.getType();
            Map<String, BigDecimal> budgets = context.deserialize(
                jsonObject.get("budgets"),
                budgetMapType
            );
            if (budgets != null) {
                for (Map.Entry<String, BigDecimal> entry : budgets.entrySet()) {
                    wallet.setBudget(entry.getKey(), entry.getValue());
                }
            }

            return wallet;
        }
    }

    // Сохранение данных в файл
    @Override
    public void saveData(Map<String, User> users) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // Загрузка данных из файла
    @Override
    public Map<String, User> loadData() {
        try {
            if (!Files.exists(Paths.get(FILE_PATH))) {
                Map<String, User> emptyMap = new HashMap<>();
                saveData(emptyMap);
                return emptyMap;
            }

            String content = Files.readString(Paths.get(FILE_PATH)).trim();
            if (content.isEmpty()) {
                Map<String, User> emptyMap = new HashMap<>();
                saveData(emptyMap);
                return emptyMap;
            }

            try (
                Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH))
            ) {
                Type type = new TypeToken<Map<String, User>>() {}.getType();
                Map<String, User> loadedUsers = gson.fromJson(reader, type);
                return loadedUsers != null ? loadedUsers : new HashMap<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            return new HashMap<>();
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing JSON data: " + e.getMessage());
            Map<String, User> emptyMap = new HashMap<>();
            saveData(emptyMap);
            return emptyMap;
        }
    }

    // Операции с пользователями
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
