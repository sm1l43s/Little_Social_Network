package ru.astondevs.socialnetwork.thymeleaffrontendservice.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request.AddPostCommentDto;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request.AddPostDto;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.response.CommentInfoResponseDto;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.UserPost;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class UserPostService {

    @Value("${service-locations.user-post-service.endpoints.posts.base-url}")
    private String userPostsServiceUrl;

    @Value("${service-locations.user-post-service.endpoints.posts.mappings.by-subscriptions}")
    private String getUserPostsBySubscriptionsUrl;

    @Value("${service-locations.user-post-service.endpoints.posts.mappings.add-comment}")
    private String addPostCommentUrl;

    private final RestClient restClient;

    public List<UserPost> sendGetAllUserPostsRequest(String jwtToken) {
        return restClient.get()
                .uri(userPostsServiceUrl)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public List<UserPost> sendGetAllUserPostsRequest(UUID userId, String jwtToken) {
        return restClient.get()
                .uri(userPostsServiceUrl + "/" + userId)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public List<UserPost> sendGetAllPostsByUserIdsRequest(Set<UUID> ids, String jwtToken) {
        var url = getUserPostsBySubscriptionsUrl;

        var uri = UriComponentsBuilder.fromUriString(url)
                .queryParam("ids", ids)
                .build()
                .toUri();

        return restClient.get()
                .uri(uri)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @SneakyThrows
    public void sendAddPostRequest(String jwtToken, AddPostDto addPostDto) {
        var multiPartData = new LinkedMultiValueMap<>();

        if (addPostDto.file() != null) {
            var image = addPostDto.file();

            var byteArrayResource = convertToByteArrayResource(image);

            multiPartData.add("image", new HttpEntity<>(byteArrayResource, createFileHeaders(image)));
        }

        multiPartData.add("data", new HttpEntity<>(addPostDto, createJsonHeaders()));

        restClient.post()
                .uri(userPostsServiceUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .body(multiPartData)
                .retrieve()
                .toBodilessEntity();
    }

    private HttpHeaders createJsonHeaders() {
        var headers = new HttpHeaders();

        headers.setContentType(APPLICATION_JSON);

        return headers;
    }

    private HttpHeaders createFileHeaders(MultipartFile file) {
        var headers = new HttpHeaders();

        var mediaType = MediaType.parseMediaType(
                Objects.requireNonNull(file.getContentType()));

        headers.setContentType(mediaType);

        return headers;
    }

    @SneakyThrows
    private ByteArrayResource convertToByteArrayResource(MultipartFile file) {
        var fileName = file.getOriginalFilename();

        if (fileName != null && !fileName.isEmpty()) {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        }

        return new ByteArrayResource(file.getBytes());
    }

    public void sendDeletePostRequest(Long postId, String jwtToken) {
        restClient.delete()
                .uri(userPostsServiceUrl + "/" + postId)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .toBodilessEntity();
    }

    public CommentInfoResponseDto sendAddCommentRequest(AddPostCommentDto addPostCommentDto, String jwtToken) {
        return restClient.post()
                .uri(addPostCommentUrl)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .body(addPostCommentDto)
                .retrieve()
                .body(CommentInfoResponseDto.class);
    }
}
