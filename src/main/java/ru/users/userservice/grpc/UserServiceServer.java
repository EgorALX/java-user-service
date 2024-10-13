package ru.users.userservice.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.users.userservice.controller.exception.model.NotFoundException;
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

    private final GrpcMapper grpcMapper;

    @Override
    public void getUsers(GetUsersRequest request, StreamObserver<GetUsersResponse> responseObserver) {
        log.info("Received getUsers request: name={}, surname={}, registration_date={}, page={}",
                request.getName(), request.getSurname(), request.getRegistrationDate(), request.getPage());

        try {
            LocalDate registrationDate = (request.hasRegistrationDate()) ? Instant.ofEpochSecond(request.getRegistrationDate().getSeconds())
                    .atZone(ZoneId.systemDefault()).toLocalDate() : null;
            int pageNumber = request.getSize();
            int pageSize = (request.getPage() == 0) ? 10 : request.getPage();
            String name = (!request.getName().isEmpty()) ? request.getName() : null;
            String surname = (!request.getSurname().isEmpty()) ? request.getSurname() : null;
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

            List<UserDto> users = userService.getUsers(name, surname, registrationDate, pageRequest);
            log.info("Found {} users matching criteria", users.size());

            GetUsersResponse.Builder responseBuilder = GetUsersResponse.newBuilder();
            users.stream().map(grpcMapper::convertUserDtoToUserResponse).forEach(responseBuilder::addUsers);
            GetUsersResponse response = responseBuilder.build();
            responseObserver.onNext(response);
            log.info("Sent getUsers response");
        } catch (NotFoundException e) {
            log.warn("User not found", e);
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error occurred while getting users", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getById(GetByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("Received getById request for user ID: {}", request.getUserId());

        try {
            UserDto user = userService.getById(request.getUserId());
            log.info("Found user with ID: {}", user.getId());
            UserResponse response = grpcMapper.convertUserDtoToUserResponse(user);
            responseObserver.onNext(response);
        } catch (NotFoundException e) {
            log.warn("User not found", e);
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error occurred while getting user by ID", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void add(AddRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("Received add request for new user");

        try {
            NewUserDto newUser = grpcMapper.convertAddRequestToNewUserDto(request);
            UserDto addedUser = userService.add(newUser);
            log.info("Added new user with ID: {}", addedUser.getId());
            UserResponse response = grpcMapper.convertUserDtoToUserResponse(addedUser);
            responseObserver.onNext(response);
        } catch (Exception e) {
            log.error("Failed to add user", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to add user").asRuntimeException());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void update(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
        log.info("Received update request for user");
        try {
            UpdateUserDto updateUserDto = grpcMapper.convertUpdateRequestToUpdateUserDto(request);
            UpdateUserDto updatedUser = userService.update(request.getUserId(), updateUserDto);
            log.info("Updated user with ID: {}", request.getUserId());
            UpdateResponse response = grpcMapper.convertUpdateUserDtoToUpdateResponse(updatedUser);
            responseObserver.onNext(response);
        } catch (Exception e) {
            log.error("Failed to update user", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to update user").asRuntimeException());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void removeById(RemoveByIdRequest request, StreamObserver<com.google.protobuf.BoolValue> responseObserver) {
        log.info("Received removeById request for user ID: {}", request.getUserId());

        try {
            boolean success = userService.removeById(request.getUserId());
            log.info("User with ID {} exists: {}", request.getUserId(), success);
            Builder boolBuilder = BoolValue.newBuilder();
            boolBuilder.setValue(success);
            responseObserver.onNext(boolBuilder.build());
        } catch (Exception e) {
            log.error("Failed to check if user exists", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to check if user exists").asRuntimeException());
        } finally {
            responseObserver.onCompleted();
        }
    }
}