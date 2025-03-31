package ru.astondevs.socialnetwork.thymeleaffrontendservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.mappers.AuthUserInfoMapper;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.AuthUserInfo;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.services.UserPostService;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.services.UserProfileService;

import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.AUTH_USER_INFO;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.POSTS;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.PageNameUtil.HOME;

@Controller
@RequestMapping("${controller.endpoints.home.base-path}")
@RequiredArgsConstructor
public class HomeController {

    private final UserProfileService userProfileService;

    private final UserPostService userPostService;

    private final AuthUserInfoMapper authUserInfoMapper;

    @GetMapping
    public String home(@CookieValue(name = "jwt") String jwtToken, Model model) {
        var authUserInfo = getAuthUserInfo(jwtToken);

        var subscriptionProfileIds = authUserInfo.subscriptionUserIds();

        var postsByUserSubscriptions = userPostService.sendGetAllPostsByUserIdsRequest(subscriptionProfileIds, jwtToken);

        model.addAttribute(AUTH_USER_INFO, authUserInfo);
        model.addAttribute(POSTS, postsByUserSubscriptions);

        return HOME;
    }

    private AuthUserInfo getAuthUserInfo(String jwtToken) {
        var userProfile = userProfileService.sendGetUserProfileRequest(jwtToken);
        return authUserInfoMapper.toAuthUserInfo(userProfile);
    }
}
