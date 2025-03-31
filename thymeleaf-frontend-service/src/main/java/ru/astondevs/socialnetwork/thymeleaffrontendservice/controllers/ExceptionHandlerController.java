package ru.astondevs.socialnetwork.thymeleaffrontendservice.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.PageNameUtil.REDIRECT_AUTH_SIGN_IN;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResponseStatusException.class)
    public String handleException(ResponseStatusException exception) {
        if (exception.getStatusCode() == UNAUTHORIZED) {
            return REDIRECT_AUTH_SIGN_IN;
        }

        throw exception;
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public String handleException(HttpServletResponse response) {
        response.setStatus(UNAUTHORIZED.value());
        return REDIRECT_AUTH_SIGN_IN;
    }
}
