package ru.users.userservice.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
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
    public ResponseEntity<List<UserDto>> getUsers(@Parameter @Valid @RequestParam(value = "page", required = false,
                                                          defaultValue = "0") Integer page,
                                                  @Parameter @Valid @RequestParam(value = "size", required = false,
                                                          defaultValue = "10") Integer size,
                                                  @Size(max = 256) @Parameter @Valid @RequestParam(value = "name",
                                                          required = false) String name,
                                                  @Size(max = 256) @Parameter @Valid @RequestParam(value = "surname",
                                                          required = false) String surname,
                                                  @Parameter @Valid @RequestParam(value = "registration_date",
                                                          required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                      LocalDate registrationDate
    ) {
        log.info("Starting getUsers method. Getting users with params: page = {}, size = {}, name = {}, surname = {}," +
                " registrationDate {}", page, size, name, surname, registrationDate);
        PageRequest pageRequest = PageRequest.of(page, size);
        List<UserDto> users = userService.getUsers(name, surname, registrationDate, pageRequest);
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