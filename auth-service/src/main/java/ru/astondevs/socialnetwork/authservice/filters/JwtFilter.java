package ru.astondevs.socialnetwork.authservice.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.authservice.mappers.UserMapper;
import ru.astondevs.socialnetwork.authservice.models.User;
import ru.astondevs.socialnetwork.authservice.services.UserService;
import ru.astondevs.socialnetwork.authservice.utils.JwtTokenManager;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;

    private final UserService userService;

    private final UserMapper userMapper;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) {
        var header = request.getHeader(AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        var token = header.substring(7);

        jwtTokenManager.validateToken(token);

        var userId = UUID.fromString(jwtTokenManager.extractUserId(token));

        var user = getUserById(userId);

        authenticateUser(user);

        chain.doFilter(request, response);
    }

    private User getUserById(UUID id) {
        var user = new User();

        try {
            user = userService.getById(id);
        } catch (ResponseStatusException e) {
            throw new BadCredentialsException("Incorrect email or password");
        }

        return user;
    }

    private void authenticateUser(User user) {
        var authorizedUserDto = userMapper.toAuthorizedUserDto(user);

        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                authorizedUserDto,
                null,
                Collections.singleton(authorizedUserDto.role()));

        SecurityContextHolder.getContext()
                .setAuthentication(usernamePasswordAuthenticationToken);
    }
}