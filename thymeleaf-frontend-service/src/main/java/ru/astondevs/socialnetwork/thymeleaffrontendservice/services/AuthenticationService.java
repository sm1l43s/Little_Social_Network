package ru.astondevs.socialnetwork.thymeleaffrontendservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.JwtToken;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request.UserLoginDto;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request.UserRegistrationDto;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${service-locations.auth-service.endpoints.auth.mappings.sign-up}")
    private String authServiceSignUpUrl;

    @Value("${service-locations.auth-service.endpoints.auth.mappings.sign-in}")
    private String authServiceSignInUrl;

    @Value("${service-locations.auth-service.endpoints.auth.mappings.logout}")
    private String authServiceLogoutUrl;

    private final RestClient restClient;

    public JwtToken sendSignUpRequest(UserRegistrationDto userRegistrationDto, Model model) {
        return restClient.post()
                .uri(authServiceSignUpUrl)
                .contentType(APPLICATION_JSON)
                .body(userRegistrationDto)
                .retrieve()
                .onStatus(status -> status == CONFLICT, (request, clientResponse) ->
                        model.addAttribute(ERROR_MESSAGE, "Пользователь с таким email уже существует")
                )
                .onStatus(HttpStatusCode::isError, (request, clientResponse) ->
                        model.addAttribute(ERROR_MESSAGE, "При регистрации возникла ошибка")
                )
                .toEntity(JwtToken.class)
                .getBody();
    }

    public JwtToken sendSignInRequest(UserLoginDto userLoginDto, Model model) {
        return restClient.post()
                .uri(authServiceSignInUrl)
                .contentType(APPLICATION_JSON)
                .body(userLoginDto)
                .retrieve()
                .onStatus(status -> status == BAD_REQUEST, (request, clientResponse) ->
                        model.addAttribute(ERROR_MESSAGE, "Неверный логин или пароль")
                )
                .onStatus(HttpStatusCode::isError, (request, clientResponse) ->
                        model.addAttribute(ERROR_MESSAGE, "Возникла ошибка при авторизации")
                )
                .toEntity(JwtToken.class)
                .getBody();
    }

    public void sendLogoutRequest(String jwtToken) {
        restClient.post()
                .uri(authServiceLogoutUrl)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .onStatus(status -> status == FORBIDDEN, (request, clientResponse) -> {
                    throw new ResponseStatusException(UNAUTHORIZED);
                })
                .toBodilessEntity();
    }
}
