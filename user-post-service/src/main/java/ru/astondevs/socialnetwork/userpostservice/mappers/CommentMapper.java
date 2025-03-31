package ru.astondevs.socialnetwork.userpostservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.astondevs.socialnetwork.userpostservice.dto.UserProfileInfoDto;
import ru.astondevs.socialnetwork.userpostservice.dto.response.CommentInfoResponseDto;
import ru.astondevs.socialnetwork.userpostservice.dto.response.CommentResponseDto;
import ru.astondevs.socialnetwork.userpostservice.models.Comment;
import ru.astondevs.socialnetwork.userpostservice.models.Post;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CommentMapper {

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "userFirstName", source = "userProfileInfoDto.firstName")
    @Mapping(target = "userLastName", source = "userProfileInfoDto.lastName")
    @Mapping(target = "userAvatarUrl", source = "userProfileInfoDto.avatarUrl")
    CommentResponseDto toCommentResponseDto(Comment comment, UserProfileInfoDto userProfileInfoDto);

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "message", source = "comment.message")
    @Mapping(target = "userId", source = "comment.userId")
    @Mapping(target = "userFirstName", source = "userProfileInfoDto.firstName")
    @Mapping(target = "userLastName", source = "userProfileInfoDto.lastName")
    @Mapping(target = "date", source = "comment.date")
    @Mapping(target = "time", source = "comment.time")
    @Mapping(target = "totalComments", expression = "java(post.getTotalComments())")
    CommentInfoResponseDto toCommentInfoResponseDto(Comment comment, UserProfileInfoDto userProfileInfoDto, Post post);
}
