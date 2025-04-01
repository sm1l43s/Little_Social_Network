package ru.astondevs.socialnetwork.userprofileservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.astondevs.socialnetwork.userprofileservice.dto.request.CreateUserProfileDto;
import ru.astondevs.socialnetwork.userprofileservice.dto.request.UpdateLastEnteredDto;
import ru.astondevs.socialnetwork.userprofileservice.dto.response.SuggestionUserInfoDto;
import ru.astondevs.socialnetwork.userprofileservice.dto.response.UserProfileInfoDto;
import ru.astondevs.socialnetwork.userprofileservice.models.UserProfile;
import ru.astondevs.socialnetwork.userprofileservice.services.UserProfileService;
import ru.astondevs.socialnetwork.userprofileservice.utils.IncomingRequestIpChecker;
import ru.astondevs.socialnetwork.userprofileservice.validation.annotations.ValidImage;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("${controller.endpoints.user-profiles.base-path}")
@RequiredArgsConstructor
public class UserProfileController {

    private final IncomingRequestIpChecker incomingRequestIpChecker;

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateUserProfileDto createUserProfileDto,
                                       HttpServletRequest request) {
        incomingRequestIpChecker.check(request);

        userProfileService.create(createUserProfileDto);

        return created(getLocationUri()).build();
    }

    private URI getLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();
    }

    @GetMapping
    public ResponseEntity<UserProfileInfoDto> getUserProfileInfoById(@AuthenticationPrincipal UserProfile userProfile) {
        return ok(userProfileService.getUserProfileInfo(userProfile.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileInfoDto> getUserProfileInfoById(@PathVariable("id") UUID id, HttpServletRequest request) {
        incomingRequestIpChecker.check(request);
        return ok(userProfileService.getUserProfileInfo(id));
    }

    @GetMapping("${controller.endpoints.user-profiles.mappings.info-mapped-by-id}")
    public ResponseEntity<Map<UUID, UserProfileInfoDto>> getUserProfilesInfoMappedById(@RequestParam Set<UUID> ids,
                                                                                       HttpServletRequest request) {
        incomingRequestIpChecker.check(request);
        return ok(userProfileService.getAllByIdsMappedById(ids));
    }

    @PostMapping("${controller.endpoints.user-profiles.mappings.set-avatar}")
    public void setAvatar(@AuthenticationPrincipal UserProfile userProfile, @ValidImage @RequestPart MultipartFile avatar) {
        userProfileService.setAvatar(userProfile.getId(), avatar);
    }

    @PostMapping("${controller.endpoints.user-profiles.mappings.update-last-entered}")
    public void updateLastEntered(@Valid @RequestBody UpdateLastEnteredDto updateLastEnteredDto,
                                  HttpServletRequest request) {
        incomingRequestIpChecker.check(request);
        userProfileService.updateLastEntered(updateLastEnteredDto);
    }

    @PostMapping("${controller.endpoints.user-profiles.mappings.subscribe}/{id}")
    public void subscribe(@AuthenticationPrincipal UserProfile userProfile, @PathVariable("id") UUID subscribeUserId) {
        userProfileService.subscribe(userProfile.getId(), subscribeUserId);
    }

    @PostMapping("${controller.endpoints.user-profiles.mappings.unsubscribe}/{id}")
    public void unsubscribe(@AuthenticationPrincipal UserProfile userProfile, @PathVariable("id") UUID unsubscribeUserId) {
        userProfileService.unsubscribe(userProfile.getId(), unsubscribeUserId);
    }

    @GetMapping("${controller.endpoints.user-profiles.mappings.suggestions}")
    public ResponseEntity<List<SuggestionUserInfoDto>> getSuggestions(@AuthenticationPrincipal UserProfile userProfile) {
        return ok(userProfileService.getSuggestions(userProfile.getId()));
    }
}
