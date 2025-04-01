package ru.astondevs.socialnetwork.userprofileservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.astondevs.socialnetwork.userprofileservice.dto.request.CreateUserProfileDto;
import ru.astondevs.socialnetwork.userprofileservice.dto.response.SuggestionUserInfoDto;
import ru.astondevs.socialnetwork.userprofileservice.dto.response.UserProfileInfoDto;
import ru.astondevs.socialnetwork.userprofileservice.models.UserProfile;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserProfileMapper {

    UserProfile toUserProfile(CreateUserProfileDto createUserProfileDto);

    @Mapping(target = "subscriptionUserIds", expression = "java(mapToUserIdSet(subscriptions))")
    @Mapping(target = "followerUserIds", expression = "java(mapToUserIdSet(followers))")
    UserProfileInfoDto toUserProfileInfoDto(UserProfile userProfile, Set<UserProfile> subscriptions, Set<UserProfile> followers);

    @Mapping(target = "followersCount", expression = "java(userProfile.getTotalFollowers())")
    SuggestionUserInfoDto toSuggestionUserInfoDto(UserProfile userProfile);

    default Set<UUID> mapToUserIdSet(Set<UserProfile> userProfiles) {
        return userProfiles.stream()
                .map(UserProfile::getId)
                .collect(Collectors.toSet());
    }
}
