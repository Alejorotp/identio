package com.identio.mvp.application.user.usecases;

import com.identio.mvp.application.user.dto.CreateUserRequest;
import com.identio.mvp.application.user.dto.UpdateUserRequest;
import com.identio.mvp.application.user.dto.UpdateUserStatusRequest;
import com.identio.mvp.application.user.dto.UserResponse;
import com.identio.mvp.domain.user.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface UserUseCase {
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(UUID id);
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(UUID id, UpdateUserRequest request);
    UserResponse updateStatus(UUID id, UpdateUserStatusRequest request);
    void deleteUser(UUID id, UserStatus status);
}
