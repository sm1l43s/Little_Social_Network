package ru.astondevs.socialnetwork.userprofileservice.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.astondevs.socialnetwork.userprofileservice.models.UserProfile;
import ru.astondevs.socialnetwork.userprofileservice.services.UserProfileService;
import ru.astondevs.socialnetwork.userprofileservice.utils.JwtTokenManager;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserProfileService userProfileService;

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

        var userProfile = userProfileService.getById(userId);

        authenticateUserProfile(userProfile, UserAuthority.fromString(userRole));

        chain.doFilter(request, response);
    }

    private void authenticateUserProfile(UserProfile userProfile, UserAuthority role) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userProfile,
                null,
                Collections.singleton(role));

        SecurityContextHolder.getContext()
                .setAuthentication(usernamePasswordAuthenticationToken);
    }

    private enum UserAuthority implements GrantedAuthority {
        USER;

        @Override
        public String getAuthority() {
            return name();
        }

        private static UserAuthority fromString(String authority) {
            for (var userAuthority : UserAuthority.values()) {
                if (userAuthority.name().equalsIgnoreCase(authority)) {
                    return userAuthority;
                }
            }

            throw new IllegalArgumentException("No enum constant " + UserAuthority.class.getName() + "." + authority);
        }
    }
}