package ru.astondevs.socialnetwork.userprofileservice.exception;

public class CustomMinioException extends RuntimeException {

    public CustomMinioException(String message, Throwable cause) {
        super(message, cause);
    }
}
