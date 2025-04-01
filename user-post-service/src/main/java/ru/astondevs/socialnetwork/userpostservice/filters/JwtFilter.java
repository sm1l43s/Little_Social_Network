package ru.astondevs.socialnetwork.userpostservice.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.astondevs.socialnetwork.userpostservice.dto.AuthUser;
import ru.astondevs.socialnetwork.userpostservice.utils.JwtTokenManager;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;

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
        var userRole = jwtTokenManager.extractRole(token);

        var authUser = new AuthUser(userId, AuthUser.Role.fromString(userRole));

        authenticateUserProfile(authUser);

        chain.doFilter(request, response);
    }

    private void authenticateUserProfile(AuthUser authUser) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                authUser,
                null,
                Collections.singleton(authUser.role()));

        SecurityContextHolder.getContext()
                .setAuthentication(usernamePasswordAuthenticationToken);
    }
}