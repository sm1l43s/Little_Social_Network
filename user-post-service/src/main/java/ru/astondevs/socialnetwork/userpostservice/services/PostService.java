package ru.astondevs.socialnetwork.userpostservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.userpostservice.dto.UserProfileInfoDto;
import ru.astondevs.socialnetwork.userpostservice.dto.request.AddPostCommentRequestDto;
import ru.astondevs.socialnetwork.userpostservice.dto.request.AddPostRequestDto;
import ru.astondevs.socialnetwork.userpostservice.dto.response.CommentInfoResponseDto;
import ru.astondevs.socialnetwork.userpostservice.dto.response.PostResponseDto;
import ru.astondevs.socialnetwork.userpostservice.feign_client.ProfileClient;
import ru.astondevs.socialnetwork.userpostservice.mappers.CommentMapper;
import ru.astondevs.socialnetwork.userpostservice.mappers.PostMapper;
import ru.astondevs.socialnetwork.userpostservice.models.Comment;
import ru.astondevs.socialnetwork.userpostservice.models.Post;
import ru.astondevs.socialnetwork.userpostservice.repositories.PostRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostMapper postMapper;

    private final CommentService commentService;

    private final CommentMapper commentMapper;

    private final ProfileClient profileClient;

    private final MinioService minioService;

    public PostResponseDto getPostById(Long postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post not found"));

        var authorProfileInfoDto = profileClient.getUserProfileInfo(post.getUserId());

        var commentAuthorIds = getCommentAuthorIds(post);

        var userProfilesInfoMappedById = profileClient.getUserProfilesInfoMappedById(commentAuthorIds);

        return getPostResponseDto(post, userProfilesInfoMappedById, authorProfileInfoDto);
    }

    public List<PostResponseDto> getAllPosts(UUID userId) {
        var posts = postRepository.findPostsByUserId(userId);

        var authorProfileInfoDto = profileClient.getUserProfileInfo(userId);

        var commentAuthorIds = getCommentAuthorIds(posts);

        var userProfilesInfoMappedById = profileClient.getUserProfilesInfoMappedById(commentAuthorIds);

        return posts.stream()
                .map(post -> getPostResponseDto(post, userProfilesInfoMappedById, authorProfileInfoDto))
                .toList();
    }

    public List<PostResponseDto> getPostsByUserSubscriptions(Set<UUID> ids) {
        var posts = postRepository.findPostsByIds(ids);

        var postsAuthorProfilesInfo = profileClient.getUserProfilesInfoMappedById(ids);

        var commentAuthorIds = getCommentAuthorIds(posts);

        var commentsAuthorProfilesInfo = profileClient.getUserProfilesInfoMappedById(commentAuthorIds);

        return posts.stream()
                .map(post -> getPostResponseDto(
                        post,
                        commentsAuthorProfilesInfo,
                        postsAuthorProfilesInfo.get(post.getUserId())))
                .toList();
    }

    private PostResponseDto getPostResponseDto(Post post, Map<UUID, UserProfileInfoDto> userProfilesInfoMappedById,
                                               UserProfileInfoDto userProfileInfoDto) {
        var commentResponseDtos = post.getComments().stream()
                .filter(comment -> userProfilesInfoMappedById.containsKey(comment.getUserId()))
                .map(comment -> {
                    var commentAuthorProfileInfo = userProfilesInfoMappedById.get(comment.getUserId());
                    return commentMapper.toCommentResponseDto(comment, commentAuthorProfileInfo);
                })
                .toList();

        return postMapper.toPostResponseDto(post, commentResponseDtos, userProfileInfoDto);
    }

    private HashSet<UUID> getCommentAuthorIds(Post post) {
        var userIds = new HashSet<UUID>();

        var comments = post.getComments();

        for (var comment : comments) {
            userIds.add(comment.getUserId());
        }

        return userIds;
    }

    private HashSet<UUID> getCommentAuthorIds(List<Post> posts) {
        var userIds = new HashSet<UUID>();

        for (var post : posts) {
            var comments = post.getComments();

            for (var comment : comments) {
                userIds.add(comment.getUserId());
            }
        }

        return userIds;
    }

    public void addPost(UUID userId, AddPostRequestDto addPostRequestDto) {
        var post = Post.builder()
                .userId(userId)
                .message(addPostRequestDto.message())
                .build();

        postRepository.save(post);
    }

    public void addPost(UUID userId, AddPostRequestDto addPostRequestDto, MultipartFile image) {
        var imageUrl = minioService.uploadFile(image);

        var post = Post.builder()
                .userId(userId)
                .message(addPostRequestDto.message())
                .imageUrl(imageUrl)
                .build();

        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post not found"));

        var postImageUrl = post.getImageUrl();

        if (postImageUrl != null) {
            minioService.deleteFile(postImageUrl);
        }

        postRepository.delete(post);
    }

    @Transactional
    public CommentInfoResponseDto addComment(AddPostCommentRequestDto addPostCommentRequestDto, UUID userId) {
        var post = postRepository.findWithCommentsById(addPostCommentRequestDto.postId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post not found"));

        var comment = commentService.save(userId, addPostCommentRequestDto.comment(), post);

        post.getComments().add(comment);

        postRepository.saveAndFlush(post);

        var authorProfileInfoDto = profileClient.getUserProfileInfo(userId);

        return commentMapper.toCommentInfoResponseDto(comment, authorProfileInfoDto, post);
    }
}
