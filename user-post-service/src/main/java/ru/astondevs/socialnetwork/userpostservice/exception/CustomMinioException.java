package ru.astondevs.socialnetwork.userpostservice.exception;

public class CustomMinioException extends RuntimeException {

    public CustomMinioException(String message, Throwable cause) {
        super(message, cause);
    }
}
