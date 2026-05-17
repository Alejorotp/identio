package com.identio.mvp.interfaces.user.grpc;

import com.identio.mvp.application.user.usecases.UserUseCase;
import com.identio.mvp.domain.user.enums.UserStatus;
import com.identio.mvp.interfaces.user.grpc.user.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserUseCase userUseCase;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void getAllUsers(GetAllUsersRequest request, StreamObserver<GetAllUsersResponse> responseObserver) {
        log.info("gRPC GetAllUsers request received");
        int page = request.getPage() > 0 ? request.getPage() : 0;
        int size = request.getSize() > 0 ? request.getSize() : 20;

        Page<com.identio.mvp.application.user.dto.UserResponse> userPage = userUseCase.getAllUsers(PageRequest.of(page, size));

        GetAllUsersResponse response = GetAllUsersResponse.newBuilder()
                .addAllUsers(userPage.getContent().stream().map(this::mapToProtoResponse).collect(Collectors.toList()))
                .setTotalElements(userPage.getTotalElements())
                .setTotalPages(userPage.getTotalPages())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #request.id == authentication.name")
    public void getUserById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("gRPC GetUserById request received for id: {}", request.getId());
        var dtoResponse = userUseCase.getUserById(UUID.fromString(request.getId()));
        responseObserver.onNext(mapToProtoResponse(dtoResponse));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("gRPC CreateUser request received for email: {}", request.getEmail());
        var dtoRequest = new com.identio.mvp.application.user.dto.CreateUserRequest(
                request.getFullName(),
                request.getEmail(),
                request.getPassword(),
                new HashSet<>(request.getRolesList())
        );
        var dtoResponse = userUseCase.createUser(dtoRequest);
        responseObserver.onNext(mapToProtoResponse(dtoResponse));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #request.id == authentication.name")
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("gRPC UpdateUser request received for id: {}", request.getId());
        var dtoRequest = new com.identio.mvp.application.user.dto.UpdateUserRequest(
                request.getFullName(),
                request.getPassword(),
                new HashSet<>(request.getRolesList())
        );
        var dtoResponse = userUseCase.updateUser(UUID.fromString(request.getId()), dtoRequest);
        responseObserver.onNext(mapToProtoResponse(dtoResponse));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void updateUserStatus(UpdateUserStatusRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("gRPC UpdateUserStatus request received for id: {}", request.getId());
        var dtoRequest = new com.identio.mvp.application.user.dto.UpdateUserStatusRequest(
                UserStatus.valueOf(request.getStatus().toUpperCase())
        );
        var dtoResponse = userUseCase.updateStatus(UUID.fromString(request.getId()), dtoRequest);
        responseObserver.onNext(mapToProtoResponse(dtoResponse));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        log.info("gRPC DeleteUser request received for id: {}", request.getId());
        UserStatus status = UserStatus.DELETED;
        if (!request.getStatus().isBlank()) {
            status = UserStatus.valueOf(request.getStatus().toUpperCase());
        }
        userUseCase.deleteUser(UUID.fromString(request.getId()), status);
        responseObserver.onNext(DeleteUserResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }

    private UserResponse mapToProtoResponse(com.identio.mvp.application.user.dto.UserResponse dto) {
        return UserResponse.newBuilder()
                .setId(dto.id().toString())
                .setEmail(dto.email())
                .setFullName(dto.fullName())
                .setStatus(dto.status().name())
                .addAllRoles(dto.roles())
                .build();
    }
}
