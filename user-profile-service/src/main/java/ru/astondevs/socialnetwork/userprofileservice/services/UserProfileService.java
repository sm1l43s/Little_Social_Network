package ru.astondevs.socialnetwork.userprofileservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.userprofileservice.dto.request.CreateUserProfileDto;
import ru.astondevs.socialnetwork.userprofileservice.dto.request.UpdateLastEnteredDto;
import ru.astondevs.socialnetwork.userprofileservice.dto.response.SuggestionUserInfoDto;
import ru.astondevs.socialnetwork.userprofileservice.dto.response.UserProfileInfoDto;
import ru.astondevs.socialnetwork.userprofileservice.mappers.UserProfileMapper;
import ru.astondevs.socialnetwork.userprofileservice.models.UserProfile;
import ru.astondevs.socialnetwork.userprofileservice.repositories.UserProfileRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final UserProfileMapper userProfileMapper;

    private final MinioService minioService;

    @Transactional
    public void create(CreateUserProfileDto createUserProfileDto) {
        var userProfile = userProfileMapper.toUserProfile(createUserProfileDto);

        try {
            userProfileRepository.create(userProfile);
            setDefaultAvatar(userProfile.getId());
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(CONFLICT, "User profile already exists");
        }
    }

    public void setDefaultAvatar(UUID userId) {
        userProfileRepository.setAvatar(userId, minioService.getDefaultAvatarUrl());
    }

    @Transactional
    public void setAvatar(UUID userId, MultipartFile image) {
        var userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User profile not found"));

        var oldAvatarUrl = userProfile.getAvatarUrl();

        minioService.deleteFile(oldAvatarUrl);

        var newAvatarUrl = minioService.uploadFile(image);

        userProfileRepository.setAvatar(userId, newAvatarUrl);
    }

    public UserProfile getById(UUID id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User profile not found"));
    }

    @Transactional(readOnly = true)
    public Map<UUID, UserProfileInfoDto> getAllByIdsMappedById(Set<UUID> ids) {
        return userProfileRepository.findAllById(ids).stream()
                .map(userProfile -> userProfileMapper.toUserProfileInfoDto(
                        userProfile,
                        userProfile.getSubscriptions(),
                        userProfile.getFollowers())
                )
                .collect(Collectors.toMap(UserProfileInfoDto::id, Function.identity()));
    }

    @Transactional
    public void updateLastEntered(UpdateLastEnteredDto updateLastEnteredDto) {
        userProfileRepository.updateLastEntered(updateLastEnteredDto.userId(), updateLastEnteredDto.lastEntered());
    }

    @Transactional(readOnly = true)
    public UserProfileInfoDto getUserProfileInfo(UUID userId) {
        return userProfileRepository.findUserProfileInfo(userId)
                .map(profile -> userProfileMapper.toUserProfileInfoDto(
                        profile,
                        profile.getSubscriptions(),
                        profile.getFollowers())
                )
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User profile not found"));
    }

    @Transactional
    public void subscribe(UUID userId, UUID subscribeUserId) {
        userProfileRepository.subscribe(userId, subscribeUserId);
    }

    @Transactional
    public void unsubscribe(UUID userId, UUID unsubscribeUserId) {
        userProfileRepository.unsubscribe(userId, unsubscribeUserId);
    }

    @Transactional(readOnly = true)
    public List<SuggestionUserInfoDto> getSuggestions(UUID userId) {
        var randomUserProfileIds = userProfileRepository.findRandomUserIdsExceptUserId(userId);

        return userProfileRepository.findUserProfilesByIds(randomUserProfileIds).stream()
                .map(userProfileMapper::toSuggestionUserInfoDto)
                .toList();
    }
}
