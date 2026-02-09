package ru.otus.hw.exceptions;

public class DbUnavailableException extends RuntimeException {

    public DbUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
