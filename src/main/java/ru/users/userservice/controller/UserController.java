package ru.users.userservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.users.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.users.userservice.model.NewUserDto;
import ru.users.userservice.model.UpdateUserDto;
import ru.users.userservice.model.UserDto;
import ru.users.userservice.model.ParamsUserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@Validated
@Tag(name = "UserController_methods")
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@RequestBody NewUserDto newUserDto) {
        log.info("Starting add method. Creating user: {}", newUserDto.toString());
        UserDto user = userService.add(newUserDto);
        log.info("Completed add method successfully. Result: {}", user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Override
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Starting removeById method. removing userId={}", userId);
        boolean isRemoved = userService.removeById(userId);
        if (!isRemoved) return ResponseEntity.noContent().build();
        log.info("Completed removeById method successfully");
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        log.info("Starting getById method. Getting user by userId={}", userId);
        UserDto user = userService.getById(userId);
        log.info("Completed getById method successfully. Result: {}", user);
        return ResponseEntity.ok(user);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@ParameterObject ParamsUserDto queryParams) {
        log.info("Starting getUsers method. Getting users with params: {}", queryParams);
        PageRequest pageRequest = PageRequest.of(queryParams.getPage(), queryParams.getSize());
        List<UserDto> users = userService.getUsers(queryParams.getName(), queryParams.getSurname(),
                queryParams.getRegistrationDate(), pageRequest);
        log.info("Completed getUsers method successfully. Results: {}", users);
        return ResponseEntity.ok(users);
    }

    @Override
    @PatchMapping("/{userId}")
    public ResponseEntity<UpdateUserDto> updateUser(@PathVariable Long userId,
                                                    @RequestBody UpdateUserDto updateUserDto) {
        log.info("Starting update method. Updating userId={}", userId);
        UpdateUserDto user = userService.update(userId, updateUserDto);
        log.info("Completed update method successfully. Result: {}", user);
        return ResponseEntity.ok(user);
    }
}