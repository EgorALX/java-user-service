package ru.users.userservice.grpc;

import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.users.userservice.grpc.UserServiceGrpc.UserServiceImplBase;
import ru.users.userservice.model.NewUserDto;
import ru.users.userservice.model.UpdateUserDto;
import ru.users.userservice.model.UserDto;
import ru.users.userservice.service.UserService;
import ru.users.userservice.grpc.UserServiceOuterClass.GetUsersResponse;
import ru.users.userservice.grpc.UserServiceOuterClass.GetUsersRequest;
import ru.users.userservice.grpc.UserServiceOuterClass.UserResponse;
import ru.users.userservice.grpc.UserServiceOuterClass.GetByIdRequest;
import ru.users.userservice.grpc.UserServiceOuterClass.AddRequest;
import ru.users.userservice.grpc.UserServiceOuterClass.UpdateRequest;
import ru.users.userservice.grpc.UserServiceOuterClass.UpdateResponse;
import ru.users.userservice.grpc.UserServiceOuterClass.RemoveByIdRequest;
import com.google.protobuf.BoolValue.Builder;
import com.google.protobuf.BoolValue;

import java.time.*;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@GrpcService
public class UserServiceServer extends UserServiceImplBase {

    private final UserService userService;

    @Override
    public void getUsers(GetUsersRequest request, StreamObserver<GetUsersResponse> responseObserver) {
        LocalDate registrationDate = null;
        if (request.hasRegistrationDate()) {
            Instant instant = Instant.ofEpochSecond(request.getRegistrationDate().getSeconds());
            registrationDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        }
        int pageSize = request.getPage();
        if (pageSize == 0) {
            pageSize = 10;
        }
        int pageNumber = request.getSize();
        if (pageNumber == 0) {
            pageNumber = 10;
        }
        String name = null;
        if (!request.getName().isBlank()) {
            name = request.getName();
        }
        String surname = null;
        if (!request.getSurname().isBlank()) {
            surname = request.getSurname();
        }
        PageRequest pageRequest = PageRequest.of(10, 10);
        List<UserDto> users = userService.getUsers(
                name,
                surname,
                registrationDate,
                pageRequest
        );
        GetUsersResponse.Builder responseBuilder = GetUsersResponse.newBuilder();
        for (UserDto user : users) {
            responseBuilder.addUsers(convertToProto(user));
        }
        GetUsersResponse response = responseBuilder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getById(GetByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        log.debug("Received GetById request for user ID: {}", request.getUserId());
        var user = userService.getById(request.getUserId());
        log.debug("Found user: {}, or not found", user != null ? user : "null");

        if (user != null) {
            var response = convertToProto(user);
            log.info("Returning user data for ID: {}", user.getId());
            responseObserver.onNext(response);
        } else {
            log.warn("User not found for ID: {}", request.getUserId());
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        }

        log.debug("Completed GetById operation");
        responseObserver.onCompleted();
    }

    @Override
    public void add(AddRequest request, StreamObserver<UserResponse> responseObserver) {
        NewUserDto newUser = convertFromProto(request);
        UserDto addedUser = userService.add(newUser);
        if (addedUser != null) {
            UserResponse response = convertToProto(addedUser);
            responseObserver.onNext(response);
        } else {
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to add user").asRuntimeException());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void update(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
        UpdateUserDto updateUser = convertFromProto(request);
        UpdateUserDto updatedUser = userService.update(request.getUserId(), updateUser);

        if (updatedUser != null) {
            UpdateResponse response = convertToProto(updatedUser);
            responseObserver.onNext(response);
        } else {
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to update user").asRuntimeException());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void removeById(RemoveByIdRequest request, StreamObserver<com.google.protobuf.BoolValue> responseObserver) {
        boolean success = userService.removeById(request.getUserId());

        Builder boolBuilder = BoolValue.newBuilder();
        boolBuilder.setValue(success);

        responseObserver.onNext(boolBuilder.build());
        responseObserver.onCompleted();
    }

    private UserResponse convertToProto(UserDto user) {
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

    private NewUserDto convertFromProto(AddRequest request) {
        LocalDate registrationDate = Instant.ofEpochSecond(request.getRegistrationDate().getSeconds())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return new NewUserDto(
                request.getName(),
                request.getSurname(),
                registrationDate
        );
    }

    private UpdateUserDto convertFromProto(UpdateRequest request) {
        LocalDate registrationDate = null;
        if (request.hasRegistrationDate()) {
            registrationDate = Instant.ofEpochSecond(request.getRegistrationDate().getSeconds())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName(request.getName());
        updateUserDto.setSurname(request.getSurname());
        updateUserDto.setRegistrationDate(registrationDate);
        return updateUserDto;
    }

    private UpdateResponse convertToProto(UpdateUserDto user) {
        LocalDate registrationDate = user.getRegistrationDate();
        ZonedDateTime zdt = registrationDate != null ? registrationDate.atStartOfDay(ZoneOffset.UTC) : null;
        return UpdateResponse.newBuilder()
                .setName(user.getName())
                .setSurname(user.getSurname())
                .setRegistrationDate(zdt != null ? Timestamp.newBuilder()
                        .setSeconds(zdt.toInstant().getEpochSecond())
                        .build() : null)
                .build();
    }
}