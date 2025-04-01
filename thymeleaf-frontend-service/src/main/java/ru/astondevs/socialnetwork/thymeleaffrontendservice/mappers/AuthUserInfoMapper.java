package ru.astondevs.socialnetwork.thymeleaffrontendservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.AuthUserInfo;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.SuggestionUserInfo;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.UserPost;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.UserProfile;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface AuthUserInfoMapper {

    @Mapping(target = "posts", source = "userPosts")
    AuthUserInfo toAuthUserInfo(UserProfile userProfile, List<UserPost> userPosts, List<SuggestionUserInfo> suggestionUsersInfo);

    AuthUserInfo toAuthUserInfo(UserProfile userProfile);
}
