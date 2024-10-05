package ru.users.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.users.userservice.model.User;
import ru.users.userservice.model.NewUserDto;
import ru.users.userservice.model.UpdateUserDto;
import ru.users.userservice.model.UserDto;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(NewUserDto dto);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    @Mapping(target = "registrationDate", source = "registrationDate")
    UpdateUserDto toUpdateDto(User user);
}
