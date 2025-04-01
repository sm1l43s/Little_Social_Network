package ru.astondevs.socialnetwork.authservice.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.authservice.dto.send.CreateUserProfileDto;
import ru.astondevs.socialnetwork.authservice.dto.request.UserRegistrationDto;
import ru.astondevs.socialnetwork.authservice.dto.response.JwtToken;
import ru.astondevs.socialnetwork.authservice.mappers.UserMapper;
import ru.astondevs.socialnetwork.authservice.repositories.UserRepository;
import ru.astondevs.socialnetwork.authservice.utils.JwtTokenManager;
import java.nio.charset.Charset;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    @Value(value = "${service-locations.user-profile-service.endpoints.user-profiles.base-url}")
    private String userProfileServiceUrl;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final JwtTokenManager jwtTokenManager;

    private final PasswordEncoder passwordEncoder;

    private final RestClient restClient;

    @Transactional
    public JwtToken register(UserRegistrationDto userRegistrationDto) {
        var user = userMapper.toUser(userRegistrationDto, passwordEncoder);

        try {
            userRepository.saveAndFlush(user);

            var createUserProfileDto = userMapper.toCreateUserProfileDto(userRegistrationDto, user);

            sendCreateUserProfileRequest(createUserProfileDto);

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(CONFLICT, "User already exists");
        }

        var authorizedUserDto = userMapper.toAuthorizedUserDto(userRegistrationDto, user);

        return new JwtToken(jwtTokenManager.generateToken(authorizedUserDto));
    }

    private void sendCreateUserProfileRequest(CreateUserProfileDto createUserProfileDto) {
        restClient.post()
                .uri(userProfileServiceUrl)
                .contentType(APPLICATION_JSON)
                .body(createUserProfileDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) ->
                        handleCreateUserProfileOnErrorResponse(response, createUserProfileDto.id())
                )
                .toBodilessEntity();
    }

    @SneakyThrows
    private void handleCreateUserProfileOnErrorResponse(ClientHttpResponse response, UUID userId) {
        userRepository.deleteById(userId);

        throw new RestClientResponseException("Error while receiving request to create user profile",
                response.getStatusCode(),
                response.getStatusText(),
                response.getHeaders(),
                response.getBody().readAllBytes(),
                Charset.defaultCharset());
    }
}
