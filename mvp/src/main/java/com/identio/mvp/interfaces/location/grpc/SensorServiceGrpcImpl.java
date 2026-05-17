package com.identio.mvp.interfaces.location.grpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identio.mvp.application.location.dto.CreateSensorRequest;
import com.identio.mvp.application.location.dto.SensorResponse;
import com.identio.mvp.application.location.dto.UpdateSensorRequest;
import com.identio.mvp.application.location.dto.UpdateSensorStatusRequest;
import com.identio.mvp.application.location.usecases.SensorUseCase;
import com.identio.mvp.domain.location.enums.SensorStatus;
import com.identio.mvp.interfaces.location.grpc.sensor.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorServiceGrpcImpl extends SensorServiceGrpc.SensorServiceImplBase {

    private final SensorUseCase sensorUseCase;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void getAllSensors(GetAllSensorsRequest request, StreamObserver<GetAllSensorsResponse> responseObserver) {
        Page<SensorResponse> page = sensorUseCase.getAllSensors(PageRequest.of(request.getPage(), request.getSize()));
        
        GetAllSensorsResponse response = GetAllSensorsResponse.newBuilder()
                .addAllSensors(page.getContent().stream().map(this::mapToGrpcResponse).collect(Collectors.toList()))
                .setTotalPages(page.getTotalPages())
                .setTotalElements(page.getTotalElements())
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void getSensorById(GetSensorByIdRequest request, StreamObserver<com.identio.mvp.interfaces.location.grpc.sensor.SensorResponse> responseObserver) {
        SensorResponse sensor = sensorUseCase.getSensorById(UUID.fromString(request.getId()));
        responseObserver.onNext(mapToGrpcResponse(sensor));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void getSensorsByLocation(GetSensorsByLocationRequest request, StreamObserver<GetSensorsByLocationResponse> responseObserver) {
        List<SensorResponse> sensors = sensorUseCase.getSensorsByLocation(UUID.fromString(request.getLocationId()));
        
        GetSensorsByLocationResponse response = GetSensorsByLocationResponse.newBuilder()
                .addAllSensors(sensors.stream().map(this::mapToGrpcResponse).collect(Collectors.toList()))
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void createSensor(com.identio.mvp.interfaces.location.grpc.sensor.CreateSensorRequest request, StreamObserver<com.identio.mvp.interfaces.location.grpc.sensor.SensorResponse> responseObserver) {
        CreateSensorRequest dto = new CreateSensorRequest(
                request.getName(),
                request.getMacAddress(),
                SensorStatus.valueOf(request.getStatus()),
                request.getLocationId().isEmpty() ? null : UUID.fromString(request.getLocationId()),
                parseJson(request.getConfigJson())
        );
        SensorResponse created = sensorUseCase.createSensor(dto);
        responseObserver.onNext(mapToGrpcResponse(created));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void updateSensor(com.identio.mvp.interfaces.location.grpc.sensor.UpdateSensorRequest request, StreamObserver<com.identio.mvp.interfaces.location.grpc.sensor.SensorResponse> responseObserver) {
        UpdateSensorRequest dto = new UpdateSensorRequest(
                request.getName().isEmpty() ? null : request.getName(),
                request.getLocationId().isEmpty() ? null : UUID.fromString(request.getLocationId()),
                parseJson(request.getConfigJson())
        );
        SensorResponse updated = sensorUseCase.updateSensor(UUID.fromString(request.getId()), dto);
        responseObserver.onNext(mapToGrpcResponse(updated));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void updateSensorStatus(com.identio.mvp.interfaces.location.grpc.sensor.UpdateSensorStatusRequest request, StreamObserver<com.identio.mvp.interfaces.location.grpc.sensor.SensorResponse> responseObserver) {
        UpdateSensorStatusRequest dto = new UpdateSensorStatusRequest(SensorStatus.valueOf(request.getStatus()));
        SensorResponse updated = sensorUseCase.updateStatus(UUID.fromString(request.getId()), dto);
        responseObserver.onNext(mapToGrpcResponse(updated));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSensor(DeleteSensorRequest request, StreamObserver<DeleteSensorResponse> responseObserver) {
        sensorUseCase.deleteSensor(UUID.fromString(request.getId()));
        responseObserver.onNext(DeleteSensorResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }

    private com.identio.mvp.interfaces.location.grpc.sensor.SensorResponse mapToGrpcResponse(SensorResponse dto) {
        var builder = com.identio.mvp.interfaces.location.grpc.sensor.SensorResponse.newBuilder()
                .setId(dto.id().toString())
                .setName(dto.name())
                .setMacAddress(dto.macAddress())
                .setStatus(dto.status().name());
        
        if (dto.locationId() != null) builder.setLocationId(dto.locationId().toString());
        if (dto.config() != null) builder.setConfigJson(toJson(dto.config()));

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
