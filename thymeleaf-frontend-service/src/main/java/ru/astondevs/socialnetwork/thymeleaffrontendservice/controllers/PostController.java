package ru.astondevs.socialnetwork.thymeleaffrontendservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request.AddPostCommentDto;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request.AddPostDto;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.response.CommentInfoResponseDto;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.mappers.AuthUserInfoMapper;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.AuthUserInfo;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.services.UserPostService;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.services.UserProfileService;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.validation.validators.ImageValidator;

import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.AUTH_USER_INFO;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.POSTS;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.POST_ERROR_MESSAGE;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.SUGGESTION_USERS_INFO;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.USER_INFO;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.PageNameUtil.REDIRECT_USER_PROFILE;

@Controller
@RequestMapping("${controller.endpoints.posts.base-path}")
@RequiredArgsConstructor
public class PostController {

    private final UserProfileService userProfileService;

    private final UserPostService userPostService;

    private final AuthUserInfoMapper authUserInfoMapper;

    @ModelAttribute(AUTH_USER_INFO)
    public void checkAuthUser(@CookieValue(name = "jwt") String jwtToken, RedirectAttributes redirectAttributes, Model model) {
        var authUserInfo = getAuthUserInfo(jwtToken);

        redirectAttributes.addFlashAttribute(AUTH_USER_INFO, authUserInfo);
        redirectAttributes.addFlashAttribute(USER_INFO, redirectAttributes.getAttribute(AUTH_USER_INFO));
        redirectAttributes.addFlashAttribute(SUGGESTION_USERS_INFO, authUserInfo.suggestionUsersInfo());
        redirectAttributes.addFlashAttribute(POSTS, authUserInfo.posts());
    }

    @PostMapping("${controller.endpoints.posts.mappings.add-post}")
    public String addPost(@Valid AddPostDto addPostDto, BindingResult bindingResult,
                          @CookieValue(name = "jwt") String jwtToken, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            var defaultMessage = bindingResult.getAllErrors()
                    .getFirst()
                    .getDefaultMessage();

            redirectAttributes.addFlashAttribute(POST_ERROR_MESSAGE, defaultMessage);
            return REDIRECT_USER_PROFILE;
        }

        if (!addPostDto.file().isEmpty()) {
            ImageValidator.validatePostImage(addPostDto.file(), redirectAttributes);
        }

        userPostService.sendAddPostRequest(jwtToken, addPostDto);

        return REDIRECT_USER_PROFILE;
    }

    @GetMapping("${controller.endpoints.posts.mappings.delete-post}/{id}")
    public String deletePost(@PathVariable("id") Long postId, @CookieValue(name = "jwt") String jwtToken,
                             RedirectAttributes ignored) {

        userPostService.sendDeletePostRequest(postId, jwtToken);
        return REDIRECT_USER_PROFILE;
    }

    @PostMapping("${controller.endpoints.posts.mappings.add-comment}")
    @ResponseBody
    public CommentInfoResponseDto addCommentToPost(@ModelAttribute AddPostCommentDto addPostCommentDto,
                                                   @CookieValue(name = "jwt") String jwtToken) {

        return userPostService.sendAddCommentRequest(addPostCommentDto, jwtToken);
    }

    private AuthUserInfo getAuthUserInfo(String jwtToken) {
        var userProfile = userProfileService.sendGetUserProfileRequest(jwtToken);
        var userPosts = userPostService.sendGetAllUserPostsRequest(jwtToken);
        var suggestionUsersInfo = userProfileService.sendGetSuggestionUsersRequest(jwtToken);

        return authUserInfoMapper.toAuthUserInfo(userProfile, userPosts, suggestionUsersInfo);
    }
}
