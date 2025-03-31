package ru.astondevs.socialnetwork.userprofileservice.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.userprofileservice.config.properties.AllowedServiceRequestProperty;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
@RequiredArgsConstructor
public class IncomingRequestIpChecker {

    private final AllowedServiceRequestProperty allowedServiceRequestProperty;

    public void check(HttpServletRequest request) {
        var ip = request.getRemoteAddr();

        var remoteAddrAllowed = allowedServiceRequestProperty.serviceLocations().values().stream()
                .map(AllowedServiceRequestProperty.ServiceConfig::ip)
                .anyMatch(ip::equals);

        if (!remoteAddrAllowed) {
            throw new ResponseStatusException(FORBIDDEN, "Ip is not in the list of allowed");
        }
    }
}
