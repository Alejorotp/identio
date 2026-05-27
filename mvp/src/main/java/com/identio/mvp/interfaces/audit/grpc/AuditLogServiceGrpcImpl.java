package com.identio.mvp.interfaces.audit.grpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identio.mvp.application.audit.dto.AuditEvent;
import com.identio.mvp.application.audit.dto.AuditLogResponse;
import com.identio.mvp.application.audit.ports.PublishAuditEventPort;
import com.identio.mvp.application.audit.usecases.GetAuditLogsUseCase;
import com.identio.mvp.domain.audit.enums.EventType;
import com.identio.mvp.interfaces.audit.grpc.audit.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceGrpcImpl extends AuditLogServiceGrpc.AuditLogServiceImplBase {

    private final GetAuditLogsUseCase getAuditLogsUseCase;
    private final PublishAuditEventPort publishAuditEventPort;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @PreAuthorize("hasRole('ADMIN') or #request.userId == authentication.name")
    public void getAuditLogs(GetAuditLogsRequest request, StreamObserver<GetAuditLogsResponse> responseObserver) {
        Page<AuditLogResponse> page;
        if (request.getUserId() != null && !request.getUserId().isEmpty()) {
            page = getAuditLogsUseCase.getAuditLogsByUserId(
                UUID.fromString(request.getUserId()),
                PageRequest.of(request.getPage(), request.getSize())
            );
        } else {
            page = getAuditLogsUseCase.getAuditLogs(PageRequest.of(request.getPage(), request.getSize()));
        }
        
        GetAuditLogsResponse response = GetAuditLogsResponse.newBuilder()
                .addAllAuditLogs(page.getContent().stream().map(this::mapToGrpcResponse).collect(Collectors.toList()))
                .setTotalPages(page.getTotalPages())
                .setTotalElements(page.getTotalElements())
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void publishAuditEvent(PublishAuditEventRequest request, StreamObserver<PublishAuditEventResponse> responseObserver) {
        AuditEvent event = new AuditEvent(
                request.getUserId().isEmpty() ? null : UUID.fromString(request.getUserId()),
                EventType.valueOf(request.getEventType()),
                parseJson(request.getDetailsJson())
        );
        
        publishAuditEventPort.publish(event);
        
        responseObserver.onNext(PublishAuditEventResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }

    private com.identio.mvp.interfaces.audit.grpc.audit.AuditLogResponse mapToGrpcResponse(AuditLogResponse dto) {
        var builder = com.identio.mvp.interfaces.audit.grpc.audit.AuditLogResponse.newBuilder()
                .setId(dto.id().toString())
                .setEventType(dto.eventType().name())
                .setTimestamp(dto.timestamp().toString());
        
        if (dto.userId() != null) builder.setUserId(dto.userId().toString());
        if (dto.details() != null) builder.setDetailsJson(toJson(dto.details()));

        return builder.build();
    }

    private Map<String, Object> parseJson(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format");
        }
    }

    private String toJson(Map<String, Object> map) {
        if (map == null) return "";
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
