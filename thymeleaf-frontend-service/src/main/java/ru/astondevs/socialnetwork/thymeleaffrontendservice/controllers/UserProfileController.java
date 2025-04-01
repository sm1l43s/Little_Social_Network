package ru.astondevs.socialnetwork.thymeleaffrontendservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.mappers.AuthUserInfoMapper;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.mappers.UserInfoMapper;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.AuthUserInfo;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.UserInfo;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.services.UserPostService;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.services.UserProfileService;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.validation.validators.ImageValidator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.AUTH_USER_INFO;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.AVATAR_ERROR_MESSAGE;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.POSTS;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.SUGGESTION_USERS_INFO;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.USER_INFO;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.PageNameUtil.PROFILE;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.PageNameUtil.REDIRECT_USER_PROFILE;

@Controller
@RequestMapping("${controller.endpoints.user-profile.base-path}")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    private final UserPostService userPostService;

    private final AuthUserInfoMapper authUserInfoMapper;

    private final UserInfoMapper userInfoMapper;

    @ModelAttribute(AUTH_USER_INFO)
    public void checkAuthUser(@CookieValue(name = "jwt") String jwtToken, Model model) {
        var authUserInfo = getAuthUserInfo(jwtToken);

        model.addAttribute(AUTH_USER_INFO, authUserInfo);
        model.addAttribute(SUGGESTION_USERS_INFO, authUserInfo.suggestionUsersInfo());
        model.addAttribute(POSTS, authUserInfo.posts());
    }

    @GetMapping
    public String getUserProfilePage(Model model) {
        model.addAttribute(USER_INFO, model.getAttribute(AUTH_USER_INFO));
        return PROFILE;
    }

    @GetMapping("/{userId}")
    public String getUserProfilePageById(@CookieValue(name = "jwt") String jwtToken,
                                         @PathVariable("userId") UUID userId, Model model) {
        var authUserInfoAttribute = Objects.requireNonNull(model.getAttribute(AUTH_USER_INFO));

        var authUserId = ((AuthUserInfo) authUserInfoAttribute).id();

        if (userId.equals(authUserId)) {
            return REDIRECT_USER_PROFILE;
        }

        var userInfo = getUserInfo(userId, jwtToken);

        model.addAttribute(USER_INFO, userInfo);
        model.addAttribute(POSTS, userInfo.posts());

        return PROFILE;
    }

    private AuthUserInfo getAuthUserInfo(String jwtToken) {
        var userProfile = userProfileService.sendGetUserProfileRequest(jwtToken);
        var userPosts = userPostService.sendGetAllUserPostsRequest(jwtToken);
        var suggestionUsersInfo = userProfileService.sendGetSuggestionUsersRequest(jwtToken);

        return authUserInfoMapper.toAuthUserInfo(userProfile, userPosts, suggestionUsersInfo);
    }

    private UserInfo getUserInfo(UUID userId, String jwtToken) {
        var userProfile = userProfileService.sendGetUserProfileRequest(userId, jwtToken);
        var userPosts = userPostService.sendGetAllUserPostsRequest(userId, jwtToken);

        return userInfoMapper.toUserInfo(userProfile, userPosts);
    }

    @PostMapping("${controller.endpoints.user-profile.mappings.set-avatar}")
    public String setAvatar(@RequestPart MultipartFile avatar, @CookieValue(name = "jwt") String jwtToken,
                            Model model, RedirectAttributes redirectAttributes) {

        ImageValidator.validateAvatar(avatar, redirectAttributes);

        if (redirectAttributes.containsAttribute(AVATAR_ERROR_MESSAGE)) {
            model.addAttribute(USER_INFO, model.getAttribute(AUTH_USER_INFO));
            return REDIRECT_USER_PROFILE;
        }

        userProfileService.sendSetAvatarRequest(jwtToken, avatar, model);

        model.addAttribute(USER_INFO, model.getAttribute(AUTH_USER_INFO));

        return REDIRECT_USER_PROFILE;
    }
}
