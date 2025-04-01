package ru.astondevs.socialnetwork.authservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import ru.astondevs.socialnetwork.authservice.config.properties.JwtProperty;
import ru.astondevs.socialnetwork.authservice.dto.AuthorizedUserDto;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

@Component
public class JwtTokenManager {

    private final JwtProperty jwtProperty;

    private final SecretKey secretKey;

    public JwtTokenManager(JwtProperty jwtProperty) {
        this.jwtProperty = jwtProperty;
        secretKey = Keys.hmacShaKeyFor(jwtProperty.key().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(AuthorizedUserDto authorizedUserDto) {
        return Jwts.builder()
                .subject(authorizedUserDto.id().toString())
                .claim("email", authorizedUserDto.email())
                .claim("role", authorizedUserDto.role())
                .issuedAt(new Date())
                .expiration(new Date(currentTimeMillis() + jwtProperty.expirationTime()))
                .signWith(secretKey)
                .compact();
    }

    public void validateToken(String token) {
        Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    public String extractUserId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}