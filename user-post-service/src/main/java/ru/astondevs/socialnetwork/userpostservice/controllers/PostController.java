package ru.astondevs.socialnetwork.userpostservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.astondevs.socialnetwork.userpostservice.dto.AuthUser;
import ru.astondevs.socialnetwork.userpostservice.dto.request.AddPostCommentRequestDto;
import ru.astondevs.socialnetwork.userpostservice.dto.request.AddPostRequestDto;
import ru.astondevs.socialnetwork.userpostservice.dto.response.CommentInfoResponseDto;
import ru.astondevs.socialnetwork.userpostservice.dto.response.PostResponseDto;
import ru.astondevs.socialnetwork.userpostservice.services.PostService;
import ru.astondevs.socialnetwork.userpostservice.utils.IncomingRequestIpChecker;
import ru.astondevs.socialnetwork.userpostservice.validation.validators.ImageValidator;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("${controller.endpoints.posts.base-path}")
@RequiredArgsConstructor
public class PostController {

    private final IncomingRequestIpChecker incomingRequestIpChecker;

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPostsById(@AuthenticationPrincipal AuthUser authUser) {
        return ok(postService.getAllPosts(authUser.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<PostResponseDto>> getAllPostsById(@PathVariable("id") UUID id, HttpServletRequest request) {
        incomingRequestIpChecker.check(request);
        return ok(postService.getAllPosts(id));
    }

    @GetMapping("${controller.endpoints.posts.mappings.by-subscriptions}")
    public ResponseEntity<List<PostResponseDto>> getPostsByUserSubscriptions(@RequestParam("ids") Set<UUID> userIds) {
        return ok(postService.getPostsByUserSubscriptions(userIds));
    }

    @PostMapping
    public ResponseEntity<Void> addPost(@AuthenticationPrincipal AuthUser authUser,
                        @Valid @RequestPart("data") AddPostRequestDto addPostRequestDto,
                        @RequestPart(required = false) MultipartFile image) {

        if (image != null) {
            ImageValidator.validate(image);
            postService.addPost(authUser.id(), addPostRequestDto, image);
            return created(getLocationUri()).build();
        }

        postService.addPost(authUser.id(), addPostRequestDto);

        return created(getLocationUri()).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId) {
        postService.deletePost(postId);
        return noContent().build();
    }

    @PostMapping("${controller.endpoints.posts.mappings.add-comment}")
    public ResponseEntity<CommentInfoResponseDto> addCommentToPost(@AuthenticationPrincipal AuthUser authUser,
                                                                   @RequestBody AddPostCommentRequestDto addPostCommentRequestDto) {
        var commentInfoResponseDto = postService.addComment(addPostCommentRequestDto, authUser.id());
        return created(getLocationUri()).body(commentInfoResponseDto);
    }

    private URI getLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();
    }
}
