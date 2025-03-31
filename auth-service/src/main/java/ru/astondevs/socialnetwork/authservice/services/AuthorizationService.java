package ru.astondevs.socialnetwork.authservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.authservice.dto.request.UserLoginDto;
import ru.astondevs.socialnetwork.authservice.dto.response.JwtToken;
import ru.astondevs.socialnetwork.authservice.mappers.UserMapper;
import ru.astondevs.socialnetwork.authservice.models.User;
import ru.astondevs.socialnetwork.authservice.utils.JwtTokenManager;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserService userService;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenManager jwtTokenManager;

    @Transactional
    public JwtToken authenticate(UserLoginDto userLoginDto) {
        var user = getUserByEmail(userLoginDto.email());

        if (!passwordEncoder.matches(userLoginDto.password(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect email or password");
        }

        userService.updateLastEntered(user.getId(), user.getLastEntered());

        var authorizedUserDto = userMapper.toAuthorizedUserDto(user);

        return new JwtToken(jwtTokenManager.generateToken(authorizedUserDto));
    }

    private User getUserByEmail(String email) {
        var user = new User();

        try {
            user = userService.getByEmail(email);
        } catch (ResponseStatusException e) {
            throw new BadCredentialsException("Incorrect email or password");
        }

        return user;
    }
}
