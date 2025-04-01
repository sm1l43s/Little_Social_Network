package ru.astondevs.socialnetwork.thymeleaffrontendservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.UserProfile;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.SuggestionUserInfo;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    @Value("${service-locations.user-profile-service.endpoints.user-profiles.base-url}")
    private String getUserProfileUrl;

    @Value("${service-locations.user-profile-service.endpoints.user-profiles.mappings.set-avatar}")
    private String setUserAvatarUrl;

    @Value("${service-locations.user-profile-service.endpoints.user-profiles.mappings.suggestions}")
    private String getUserSuggestionsUrl;

    private final RestClient restClient;

    public UserProfile sendGetUserProfileRequest(String jwtToken) {
        return restClient.get()
                .uri(getUserProfileUrl)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .onStatus(status -> status == FORBIDDEN, (request, clientResponse) -> {
                    throw new ResponseStatusException(UNAUTHORIZED);
                })
                .toEntity(UserProfile.class)
                .getBody();
    }

    public UserProfile sendGetUserProfileRequest(UUID userId, String jwtToken) {
        return restClient.get()
                .uri(getUserProfileUrl + "/" + userId)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .toEntity(UserProfile.class)
                .getBody();
    }

    public void sendSetAvatarRequest(String jwtToken, MultipartFile avatar, Model model) {
        var multipartBodyBuilder = new MultipartBodyBuilder();

        multipartBodyBuilder.part("avatar", avatar.getResource());

        restClient.post()
                .uri(setUserAvatarUrl)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MULTIPART_FORM_DATA)
                .body(multipartBodyBuilder.build())
                .retrieve()
                .onStatus(status -> status == BAD_REQUEST, (request, clientResponse) ->
                        model.addAttribute(ERROR_MESSAGE, "Формат: jpg, png, jpeg не более 5 мегабайт")
                )
                .onStatus(status -> status == FORBIDDEN, (request, clientResponse) -> {
                    throw new ResponseStatusException(UNAUTHORIZED);
                })
                .onStatus(HttpStatusCode::isError, (request, clientResponse) ->
                        model.addAttribute(ERROR_MESSAGE, "Не удалось установить аватар")
                )
                .toBodilessEntity();
    }

    public List<SuggestionUserInfo> sendGetSuggestionUsersRequest(String jwtToken) {
        return restClient.get()
                .uri(getUserSuggestionsUrl)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .onStatus(status -> status == FORBIDDEN, (request, clientResponse) -> {
                    throw new ResponseStatusException(UNAUTHORIZED);
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
