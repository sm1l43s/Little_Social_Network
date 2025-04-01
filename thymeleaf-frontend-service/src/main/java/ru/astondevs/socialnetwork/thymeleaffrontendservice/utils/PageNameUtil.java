package ru.astondevs.socialnetwork.thymeleaffrontendservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PageNameUtil {

    public static final String HOME = "home";
    public static final String SIGN_UP = "sign-up";
    public static final String SIGN_IN = "sign-in";
    public static final String PROFILE = "profile";

    public static final String REDIRECT_AUTH_SIGN_IN = "redirect:/auth/sign-in";
    public static final String REDIRECT_USER_PROFILE = "redirect:/user-profile";
}
