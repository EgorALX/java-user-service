package ru.users.userservice.grpc;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.users.userservice.model.NewUserDto;
import ru.users.userservice.model.UpdateUserDto;
import ru.users.userservice.model.UserDto;
import ru.users.userservice.grpc.UserServiceOuterClass.UserResponse;
import ru.users.userservice.grpc.UserServiceOuterClass.AddRequest;
import ru.users.userservice.grpc.UserServiceOuterClass.UpdateRequest;
import ru.users.userservice.grpc.UserServiceOuterClass.UpdateResponse;

import java.time.*;

@Component
public class GrpcMapper {

    public UserResponse convertUserDtoToUserResponse(UserDto user) {
        LocalDate registrationDate = user.getRegistrationDate();
        ZonedDateTime zdt = registrationDate.atStartOfDay(ZoneOffset.UTC);
        return UserResponse.newBuilder()
                .setUserId(user.getId())
                .setName(user.getName())
                .setSurname(user.getSurname())
                .setRegistrationDate(Timestamp.newBuilder()
                        .setSeconds(zdt.toInstant().getEpochSecond())
                        .setNanos(zdt.toInstant().getNano())
                        .build())
                .build();
    }

    public NewUserDto convertAddRequestToNewUserDto(AddRequest request) throws Exception {
        if (request.getName().isBlank() || request.getSurname().isBlank() || !request.hasRegistrationDate()) {
            throw new Exception("Params are not specified");
        }
        LocalDate registrationDate = Instant.ofEpochSecond(request
                        .getRegistrationDate().getSeconds()).atZone(ZoneId.systemDefault()).toLocalDate();
        return new NewUserDto(request.getName(), request.getSurname(), registrationDate);
    }

    public UpdateUserDto convertUpdateRequestToUpdateUserDto(UpdateRequest request) {
        LocalDate registrationDate = (request.hasRegistrationDate()) ? Instant.ofEpochSecond(request
                .getRegistrationDate().getSeconds()).atZone(ZoneId.systemDefault()).toLocalDate() : null;
        String name = (!request.getName().isBlank()) ? request.getName() : null;
        String surname = (!request.getSurname().isBlank()) ? request.getSurname() : null;
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName(name);
        updateUserDto.setSurname(surname);
        updateUserDto.setRegistrationDate(registrationDate);
        return updateUserDto;
    }

    public UpdateResponse convertUpdateUserDtoToUpdateResponse(UpdateUserDto user) {
        LocalDate registrationDate = user.getRegistrationDate();
        ZonedDateTime zdt = registrationDate.atStartOfDay(ZoneOffset.UTC);
        return UpdateResponse.newBuilder()
                .setName(user.getName())
                .setSurname(user.getSurname())
                .setRegistrationDate(Timestamp.newBuilder()
                        .setSeconds(zdt.toInstant().getEpochSecond())
                        .setNanos(zdt.toInstant().getNano())
                        .build())
                .build();
    }
}