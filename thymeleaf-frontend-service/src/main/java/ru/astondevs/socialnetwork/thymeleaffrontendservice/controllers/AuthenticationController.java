package ru.astondevs.socialnetwork.thymeleaffrontendservice.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.JwtToken;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request.UserLoginDto;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request.UserRegistrationDto;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.mappers.AuthUserInfoMapper;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.AuthUserInfo;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.UserPost;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.services.AuthenticationService;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.services.UserPostService;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.services.UserProfileService;
import java.util.ArrayList;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.AUTH_USER_INFO;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.ERROR_MESSAGE;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.LOGOUT;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.POSTS;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.SUGGESTION_USERS_INFO;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.USER_INFO;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.USER_INPUT;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.PageNameUtil.REDIRECT_AUTH_SIGN_IN;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.PageNameUtil.REDIRECT_USER_PROFILE;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.PageNameUtil.SIGN_IN;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.PageNameUtil.SIGN_UP;

@Controller
@RequestMapping("${controller.endpoints.auth.base-path}")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserProfileService userProfileService;
    private final UserPostService userPostService;

    private final AuthUserInfoMapper authUserInfoMapper;

    @GetMapping("${controller.endpoints.auth.mappings.sign-up}")
    public String signUp(Model model, @ModelAttribute(USER_INPUT) UserRegistrationDto userRegistrationDto) {
        model.addAttribute(USER_INPUT, userRegistrationDto);
        return SIGN_UP;
    }

    @PostMapping("${controller.endpoints.auth.mappings.sign-up}")
    public String signUp(@Valid @ModelAttribute(USER_INPUT) UserRegistrationDto userRegistrationDto,
                         BindingResult bindingResult, HttpServletResponse response, Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return SIGN_UP;
        }

        var jwtToken = authenticationService.sendSignUpRequest(userRegistrationDto, model);

        if (model.containsAttribute(ERROR_MESSAGE)) {
            return SIGN_UP;
        }

        addJwtCookie(response, jwtToken.accessToken());

        var authUserInfo = getSignUpAuthUserInfo(jwtToken);

        redirectAttributes.addFlashAttribute(AUTH_USER_INFO, authUserInfo);
        redirectAttributes.addFlashAttribute(USER_INFO, authUserInfo);
        redirectAttributes.addFlashAttribute(SUGGESTION_USERS_INFO, authUserInfo.suggestionUsersInfo());


        return REDIRECT_USER_PROFILE;
    }

    @GetMapping("${controller.endpoints.auth.mappings.sign-in}")
    public String signIn(Model model, @ModelAttribute(USER_INPUT) UserLoginDto userLoginDto) {
        model.addAttribute(USER_INPUT, userLoginDto);
        return SIGN_IN;
    }

    @PostMapping("${controller.endpoints.auth.mappings.sign-in}")
    public String signIn(@Valid @ModelAttribute(USER_INPUT) UserLoginDto userLoginDto, BindingResult bindingResult,
                         HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return SIGN_IN;
        }

        var jwtToken = authenticationService.sendSignInRequest(userLoginDto, model);

        if (model.containsAttribute(ERROR_MESSAGE)) {
            return SIGN_IN;
        }

        addJwtCookie(response, jwtToken.accessToken());

        var authUserInfo = getSignInAuthUserInfo(jwtToken);

        redirectAttributes.addFlashAttribute(AUTH_USER_INFO, authUserInfo);
        redirectAttributes.addFlashAttribute(USER_INFO, authUserInfo);
        redirectAttributes.addFlashAttribute(SUGGESTION_USERS_INFO, authUserInfo.suggestionUsersInfo());
        redirectAttributes.addFlashAttribute(POSTS, authUserInfo.posts());

        return REDIRECT_USER_PROFILE;
    }

    @GetMapping("${controller.endpoints.auth.mappings.logout}")
    public String logout(@CookieValue(name = "jwt") String jwtToken, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        authenticationService.sendLogoutRequest(jwtToken);

        deleteJwtCookie(response);

        redirectAttributes.addFlashAttribute(LOGOUT, true);

        return REDIRECT_AUTH_SIGN_IN;
    }

    private void addJwtCookie(HttpServletResponse response, String jwtToken) {
        var jwtCookie = new Cookie("jwt", jwtToken);

        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");

        response.addCookie(jwtCookie);
        response.addHeader(SET_COOKIE, jwtCookie.toString());
    }

    private void deleteJwtCookie(HttpServletResponse response) {
        var jwtCookie = new Cookie("jwt", "");

        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);
        response.addHeader(SET_COOKIE, jwtCookie.toString());
    }

    private AuthUserInfo getSignUpAuthUserInfo(JwtToken jwtToken) {
        var userProfile = userProfileService.sendGetUserProfileRequest(jwtToken.accessToken());
        var userPosts = new ArrayList<UserPost>();
        var suggestionUsersInfo = userProfileService.sendGetSuggestionUsersRequest(jwtToken.accessToken());

        return authUserInfoMapper.toAuthUserInfo(userProfile, userPosts, suggestionUsersInfo);
    }

    private AuthUserInfo getSignInAuthUserInfo(JwtToken jwtToken) {
        var userProfile = userProfileService.sendGetUserProfileRequest(jwtToken.accessToken());
        var userPosts = userPostService.sendGetAllUserPostsRequest(jwtToken.accessToken());
        var suggestionUsersInfo = userProfileService.sendGetSuggestionUsersRequest(jwtToken.accessToken());

        return authUserInfoMapper.toAuthUserInfo(userProfile, userPosts, suggestionUsersInfo);
    }
}
