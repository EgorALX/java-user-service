package ru.users.userservice.service;

import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import ru.users.userservice.model.NewUserDto;
import ru.users.userservice.model.UpdateUserDto;
import ru.users.userservice.model.UserDto;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    List<UserDto> getUsers(String name, String surname, LocalDate registrationDate, PageRequest pageRequest);

    UserDto getById(Long userId);

    UserDto add(@Valid NewUserDto dto);

    UpdateUserDto update(@Positive Long userId, @Valid UpdateUserDto dto);

    Boolean removeById(@Positive Long userId);
}
