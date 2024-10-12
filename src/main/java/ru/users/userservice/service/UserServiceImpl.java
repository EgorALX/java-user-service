package ru.users.userservice.service;

import ru.users.userservice.controller.exception.model.NotFoundException;
import ru.users.userservice.model.UserDto;
import ru.users.userservice.mapper.UserMapper;
import ru.users.userservice.model.User;
import ru.users.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.users.userservice.model.NewUserDto;
import ru.users.userservice.model.UpdateUserDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(String name, String surname, LocalDate registrationDate, PageRequest pageRequest) {
        List<User> users = userRepository.getUsersByParams(name,
                surname, registrationDate, pageRequest);
        if (users.isEmpty()) throw new NotFoundException("List of users is empty");
        return users.stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long userId) {
        return userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found.")));
    }

    @Override
    public UserDto add(NewUserDto dto) {
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(dto)));
    }

    @Override
    public UpdateUserDto update(Long userId, UpdateUserDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Optional.ofNullable(dto.getName()).ifPresent(user::setName);
        Optional.ofNullable(dto.getSurname()).ifPresent(user::setSurname);
        Optional.ofNullable(dto.getRegistrationDate()).ifPresent(user::setRegistrationDate);
        return userMapper.toUpdateDto(user);
    }

    @Override
    public Boolean removeById(Long userId) {
        try {
            userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
            userRepository.deleteById(userId);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
}
