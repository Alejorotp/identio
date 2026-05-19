package com.identio.mvp.interfaces.auth.grpc;

import com.identio.mvp.interfaces.auth.grpc.auth.AuthServiceGrpc;
import com.identio.mvp.interfaces.auth.grpc.auth.LoginRequest;
import com.identio.mvp.interfaces.auth.grpc.auth.RefreshTokenRequest;
import com.identio.mvp.interfaces.auth.grpc.auth.RegisterRequest;
import com.identio.mvp.interfaces.auth.grpc.auth.TokenResponse;
import com.identio.mvp.interfaces.auth.grpc.auth.FacialTokenRequest;
import com.identio.mvp.application.auth.usecases.AuthUseCase;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AuthServiceGrpcImpl extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthUseCase authUseCase;

    @Override
    public void register(RegisterRequest request, StreamObserver<TokenResponse> responseObserver) {
        log.info("gRPC Register request received for email: {}", request.getEmail());
        var dtoRequest = new com.identio.mvp.application.auth.dto.RegisterRequest(
                request.getFullName(),
                request.getEmail(),
                request.getPassword()
        );

        var dtoResponse = authUseCase.register(dtoRequest);

        TokenResponse response = TokenResponse.newBuilder()
                .setAccessToken(dtoResponse.accessToken())
                .setRefreshToken(dtoResponse.refreshToken())
                .setTokenType(dtoResponse.tokenType())
                .setUserId(dtoResponse.userId().toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginRequest request, StreamObserver<TokenResponse> responseObserver) {
        log.info("gRPC Login request received for email: {}", request.getEmail());
        var dtoRequest = new com.identio.mvp.application.auth.dto.LoginRequest(
                request.getEmail(),
                request.getPassword()
        );

        var dtoResponse = authUseCase.login(dtoRequest);

        TokenResponse response = TokenResponse.newBuilder()
                .setAccessToken(dtoResponse.accessToken())
                .setRefreshToken(dtoResponse.refreshToken())
                .setTokenType(dtoResponse.tokenType())
                .setUserId(dtoResponse.userId().toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void refresh(RefreshTokenRequest request, StreamObserver<TokenResponse> responseObserver) {
        log.info("gRPC Refresh request received");
        var dtoRequest = new com.identio.mvp.application.auth.dto.RefreshTokenRequest(
                request.getRefreshToken()
        );

        var dtoResponse = authUseCase.refreshToken(dtoRequest);

        TokenResponse response = TokenResponse.newBuilder()
                .setAccessToken(dtoResponse.accessToken())
                .setRefreshToken(dtoResponse.refreshToken())
                .setTokenType(dtoResponse.tokenType())
                .setUserId(dtoResponse.userId().toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void generateFacialToken(FacialTokenRequest request, StreamObserver<TokenResponse> responseObserver) {
        log.info("gRPC GenerateFacialToken request received for user ID: {}", request.getUserId());
        var dtoRequest = new com.identio.mvp.application.auth.dto.FacialTokenRequest(
                UUID.fromString(request.getUserId())
        );

        var dtoResponse = authUseCase.loginFacial(dtoRequest);

        TokenResponse response = TokenResponse.newBuilder()
                .setAccessToken(dtoResponse.accessToken())
                .setRefreshToken(dtoResponse.refreshToken())
                .setTokenType(dtoResponse.tokenType())
                .setUserId(dtoResponse.userId().toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
