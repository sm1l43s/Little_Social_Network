package ru.astondevs.socialnetwork.authservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.socialnetwork.authservice.dto.AuthorizedUserDto;
import ru.astondevs.socialnetwork.authservice.dto.request.NewPasswordDto;
import ru.astondevs.socialnetwork.authservice.services.UserService;

@RestController
@RequestMapping("${controller.endpoints.auth-user.base-path}")
@RequiredArgsConstructor
public class AuthorizedUserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @PatchMapping("${controller.endpoints.auth-user.mappings.change-password}")
    public void changePassword(@AuthenticationPrincipal AuthorizedUserDto authorizedUserDto,
                               @Valid @RequestBody NewPasswordDto newPasswordDto) {

        var encodedPassword = passwordEncoder.encode(newPasswordDto.password());
        userService.changePassword(authorizedUserDto.id(), encodedPassword);
    }
}
