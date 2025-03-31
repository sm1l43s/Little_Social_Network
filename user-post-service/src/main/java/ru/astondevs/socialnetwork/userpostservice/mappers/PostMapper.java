package ru.astondevs.socialnetwork.userpostservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.astondevs.socialnetwork.userpostservice.dto.UserProfileInfoDto;
import ru.astondevs.socialnetwork.userpostservice.dto.response.CommentResponseDto;
import ru.astondevs.socialnetwork.userpostservice.dto.response.PostResponseDto;
import ru.astondevs.socialnetwork.userpostservice.models.Post;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PostMapper {

    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "userFirstName", source = "userProfileInfoDto.firstName")
    @Mapping(target = "userLastName", source = "userProfileInfoDto.lastName")
    @Mapping(target = "userAvatarUrl", source = "userProfileInfoDto.avatarUrl")
    @Mapping(target = "userIdsWhoLiked", source = "post.userIdsWhoLiked")
    PostResponseDto toPostResponseDto(Post post, List<CommentResponseDto> commentResponseDtos, UserProfileInfoDto userProfileInfoDto);
}
