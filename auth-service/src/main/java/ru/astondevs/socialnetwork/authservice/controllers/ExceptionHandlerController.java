package ru.astondevs.socialnetwork.authservice.controllers;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.authservice.dto.response.ResponseMessage;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseMessage> handleResponseStatusException(ResponseStatusException exception) {
        log.warn(exception.getMessage(), exception);
        return getErrorResponse(exception.getStatusCode(), exception.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn(exception.getMessage(), exception);

        var fieldErrors = exception.getBindingResult().getFieldErrors();

        var errorMessage = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return getErrorResponse(BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseMessage> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn(exception.getMessage(), exception);

        if (exception.getCause() instanceof MismatchedInputException mismatchedInputException) {
            var jsonMappingExceptionReferences = mismatchedInputException.getPath();

            if (!jsonMappingExceptionReferences.isEmpty()) {
                var fieldName = jsonMappingExceptionReferences.getFirst().getFieldName();
                var message = "Invalid value for parameter: " + fieldName;
                return badRequest().body(new ResponseMessage(message));
            }
        }

        return getErrorResponse(BAD_REQUEST, "Invalid response body");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseMessage> handleBadCredentials(BadCredentialsException exception) {
        log.warn(exception.getMessage(), exception);
        return getErrorResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ResponseMessage> handleRestClientResponseException(RestClientResponseException exception) {
        log.error("{} {}", exception.getStatusCode(), exception.getResponseBodyAsString(), exception);
        return getErrorResponse();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return getErrorResponse();
    }

    private ResponseEntity<ResponseMessage> getErrorResponse() {
        var responseMessage = new ResponseMessage("An error occurred while request processing");
        return status(INTERNAL_SERVER_ERROR).body(responseMessage);
    }

    private ResponseEntity<ResponseMessage> getErrorResponse(HttpStatusCode statusCode, String message) {
        var responseMessage = new ResponseMessage(message);
        return status(statusCode).body(responseMessage);
    }
}
