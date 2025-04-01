package ru.astondevs.socialnetwork.userpostservice.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.astondevs.socialnetwork.userpostservice.dto.UserProfileInfoDto;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@FeignClient(
        name = "${service-locations.user-profile-service.url}",
        url = "${service-locations.user-profile-service.endpoints.user-profiles.base-url}")
public interface ProfileClient {

    @GetMapping("/{id}")
    UserProfileInfoDto getUserProfileInfo(@PathVariable("id") UUID id);

    @GetMapping("${service-locations.user-profile-service.endpoints.user-profiles.mappings.info-mapped-by-id}")
    Map<UUID, UserProfileInfoDto> getUserProfilesInfoMappedById(@RequestParam Set<UUID> ids);
}
