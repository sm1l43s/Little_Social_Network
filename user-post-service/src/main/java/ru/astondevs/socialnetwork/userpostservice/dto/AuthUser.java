package ru.astondevs.socialnetwork.userpostservice.dto;

import org.springframework.security.core.GrantedAuthority;
import java.util.UUID;

public record AuthUser(UUID id, Role role) {

    public enum Role implements GrantedAuthority {
        USER;

        @Override
        public String getAuthority() {
            return name();
        }

        public static Role fromString(String authority) {
            for (var userAuthority : Role.values()) {
                if (userAuthority.name().equalsIgnoreCase(authority)) {
                    return userAuthority;
                }
            }

            throw new IllegalArgumentException("No enum constant " + Role.class.getName() + "." + authority);
        }
    }
}
