package org.example;

// Исключение для обработки ошибок аутентификации
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
