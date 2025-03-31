package ru.astondevs.socialnetwork.authservice.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.astondevs.socialnetwork.authservice.dto.AuthorizedUserDto;
import ru.astondevs.socialnetwork.authservice.dto.send.CreateUserProfileDto;
import ru.astondevs.socialnetwork.authservice.dto.request.UserRegistrationDto;
import ru.astondevs.socialnetwork.authservice.models.User;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(userRegistrationDto.password()))")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    User toUser(UserRegistrationDto userRegistrationDto, PasswordEncoder passwordEncoder);

    @Mapping(target = "email", source = "userRegistrationDto.email")
    CreateUserProfileDto toCreateUserProfileDto(UserRegistrationDto userRegistrationDto, User user);

    @Mapping(target = "email", source = "userRegistrationDto.email")
    AuthorizedUserDto toAuthorizedUserDto(UserRegistrationDto userRegistrationDto, User user);

    AuthorizedUserDto toAuthorizedUserDto(User user);
}
