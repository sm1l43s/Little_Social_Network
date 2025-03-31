package ru.astondevs.socialnetwork.authservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.astondevs.socialnetwork.authservice.dto.AuthorizedUserDto;
import ru.astondevs.socialnetwork.authservice.dto.request.UserLoginDto;
import ru.astondevs.socialnetwork.authservice.dto.request.UserRegistrationDto;
import ru.astondevs.socialnetwork.authservice.dto.response.JwtToken;
import ru.astondevs.socialnetwork.authservice.services.AuthorizationService;
import ru.astondevs.socialnetwork.authservice.services.RegistrationService;
import ru.astondevs.socialnetwork.authservice.services.UserService;
import java.net.URI;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("${controller.endpoints.auth.base-path}")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;

    private final AuthorizationService authorizationService;
    private final UserService userService;

    @PostMapping("${controller.endpoints.auth.mappings.sign-up}")
    public ResponseEntity<JwtToken> signUp(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        var token = registrationService.register(userRegistrationDto);

        var locationUri = getLocationUri();

        return created(locationUri).body(token);
    }

    @PostMapping("${controller.endpoints.auth.mappings.sign-in}")
    public ResponseEntity<JwtToken> signIn(@Valid @RequestBody UserLoginDto userLoginDto) {
        var locationUri = getLocationUri();

        var jwtToken = authorizationService.authenticate(userLoginDto);

        return created(locationUri).body(jwtToken);
    }

    @PostMapping("${controller.endpoints.auth.mappings.logout}")
    public void logout(@AuthenticationPrincipal AuthorizedUserDto authorizedUserDto) {
        userService.updateLastEntered(authorizedUserDto.id(), authorizedUserDto.lastEntered());
    }

    private URI getLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();
    }
}
