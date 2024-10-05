package ru.users.userservice.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.users.userservice.controller.exception.model.NotFoundException;
import ru.users.userservice.mapper.UserMapper;
import ru.users.userservice.model.User;
import ru.users.userservice.repository.UserRepository;
import ru.users.userservice.service.UserServiceImpl;
import ru.users.userservice.model.NewUserDto;
import ru.users.userservice.model.UpdateUserDto;
import ru.users.userservice.model.UserDto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private List<User> users;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
                new User(1L, "Ivan", "Ivanov", LocalDate.of(2023, 1, 1)),
                new User(1L, "John", "Smith", LocalDate.of(2023, 1, 1))
        );

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setSurname("Smith");
        userDto.setRegistrationDate(LocalDate.of(2023, 1, 1));
        user = new User(1L, "John", "Smith", LocalDate.of(2023, 1, 1));
    }

    @Test
    void getUsersTest() {
        when(userRepository.getUsersByParams(any(String.class), any(String.class), any(LocalDate.class),
                any(PageRequest.class))).thenReturn(users);
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        List<UserDto> result = userService.getUsers("Ivan", "Ivanov",
                LocalDate.of(2023, 1, 1), PageRequest.of(1, 10));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userDto.getName(), result.get(0).getName());
        assertEquals(userDto.getSurname(), result.get(0).getSurname());
        assertEquals(userDto.getRegistrationDate(), result.get(0).getRegistrationDate());
    }

    @Test
    void getByIdTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getSurname(), result.getSurname());
        assertEquals(userDto.getRegistrationDate(), result.getRegistrationDate());
    }

    @Test
    @Transactional
    void addTest() {
        NewUserDto dto = new NewUserDto("New", "Name", LocalDate.now());
        when(userMapper.toUser(dto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.add(dto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getSurname(), result.getSurname());
        assertEquals(userDto.getRegistrationDate(), result.getRegistrationDate());
    }

    @Test
    @Transactional
    void removeByIdTest() {
        Long userId = 1L;
        userService.removeById(userId);
    }

    @Test
    @Transactional
    void updateTest() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("Updated");
        dto.setSurname("Upp");
        dto.setRegistrationDate(LocalDate.now());

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(userMapper.toUpdateDto(any(User.class))).thenReturn(dto);

        UpdateUserDto result = userService.update(1L, dto);

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertNotNull(result.getSurname());
        assertEquals(dto.getRegistrationDate(), result.getRegistrationDate());
    }

    @Test
    @Transactional
    void updateWithNullTest() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("Updated");
        dto.setSurname(null);
        dto.setRegistrationDate(LocalDate.now());
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(userMapper.toUpdateDto(any(User.class))).thenReturn(dto);

        UpdateUserDto result = userService.update(1L, dto);

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertNull(result.getSurname());
        assertEquals(dto.getRegistrationDate(), result.getRegistrationDate());
    }

    @Test
    void getByIdWithIncorrectIdTest() {
        assertThrows(NotFoundException.class, () -> userService.getById(100L));
    }

    @Test
    void updateWithIncorrectIdTest() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("Updated");
        dto.setSurname("Upp");
        dto.setRegistrationDate(LocalDate.now());
        assertThrows(NotFoundException.class, () -> userService.update(100L, dto));
    }
}
