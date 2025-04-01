package ru.astondevs.socialnetwork.authservice.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.authservice.dto.send.UpdateLastEnteredDto;
import ru.astondevs.socialnetwork.authservice.models.User;
import ru.astondevs.socialnetwork.authservice.repositories.UserRepository;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value(value = "${service-locations.user-profile-service.endpoints.user-profiles.mappings.update-last-entered}")
    private String userProfileServiceUpdateLastEnteredUrl;

    private final UserRepository userRepository;

    private final RestClient restClient;

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    @Transactional
    public void updateLastEntered(UUID userId, LocalDateTime oldLastEntered) {
        var now = now().truncatedTo(SECONDS);

        userRepository.updateLastEntered(userId, now);

        sendUpdateUserProfileLastEnteredRequest(userId, oldLastEntered, now);
    }

    private void sendUpdateUserProfileLastEnteredRequest(UUID userId, LocalDateTime oldValue, LocalDateTime newValue) {
        var updateLastEnteredDto = new UpdateLastEnteredDto(userId, newValue);

        restClient.post()
                .uri(userProfileServiceUpdateLastEnteredUrl)
                .contentType(APPLICATION_JSON)
                .body(updateLastEnteredDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) ->
                        handleUpdateLastEnteredOnErrorResponse(response, updateLastEnteredDto.userId(), oldValue)
                )
                .toBodilessEntity();
    }

    @SneakyThrows
    private void handleUpdateLastEnteredOnErrorResponse(ClientHttpResponse response, UUID userId, LocalDateTime oldValue) {
        userRepository.updateLastEntered(userId, oldValue);

        throw new RestClientResponseException("Error while receiving request to create user profile",
                response.getStatusCode(),
                response.getStatusText(),
                response.getHeaders(),
                response.getBody().readAllBytes(),
                Charset.defaultCharset());
    }

    @Transactional
    public void changePassword(UUID userId, String newPassword) {
        userRepository.changePassword(userId, newPassword);
    }
}
