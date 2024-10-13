package ru.users.userservice.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.users.userservice.model.NewUserDto;
import ru.users.userservice.model.UpdateUserDto;
import ru.users.userservice.model.UserDto;
import ru.users.userservice.mapper.UserMapper;
import ru.users.userservice.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void toUserDtoTest() {
        User user = new User(1L, "Ivan", "Ivanov", java.time.LocalDate.now());
        UserDto userDto = mapper.toUserDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getSurname(), userDto.getSurname());
        assertEquals(user.getRegistrationDate(), userDto.getRegistrationDate());
    }

    @Test
    void toNewUserTest() {
        NewUserDto userDto = new NewUserDto("Ivan", "Ivanov", java.time.LocalDate.now());
        User user = mapper.toUser(userDto);

        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getSurname(), user.getSurname());
        assertEquals(userDto.getRegistrationDate(), user.getRegistrationDate());
    }

    @Test
    void toUpdateDtoTest() {
        User user = new User(1L, "Ivan", "Ivanov", java.time.LocalDate.now());
        UpdateUserDto updateUserDto = mapper.toUpdateDto(user);

        assertEquals(user.getName(), updateUserDto.getName());
        assertEquals(user.getSurname(), updateUserDto.getSurname());
        assertEquals(user.getRegistrationDate(), updateUserDto.getRegistrationDate());
    }
}
