package ru.astondevs.socialnetwork.thymeleaffrontendservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.UserInfo;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.models.UserPost;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.UserProfile;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserInfoMapper {

    @Mapping(target = "posts", source = "userPost")
    UserInfo toUserInfo(UserProfile userProfile, List<UserPost> userPost);
}
