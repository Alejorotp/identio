package com.identio.mvp.interfaces.location.grpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identio.mvp.application.location.dto.CreateLocationRequest;
import com.identio.mvp.application.location.dto.LocationResponse;
import com.identio.mvp.application.location.dto.UpdateLocationRequest;
import com.identio.mvp.application.location.usecases.LocationUseCase;
import com.identio.mvp.interfaces.location.grpc.location.*;
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
public class LocationServiceGrpcImpl extends LocationServiceGrpc.LocationServiceImplBase {

    private final LocationUseCase locationUseCase;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void getAllLocations(GetAllLocationsRequest request, StreamObserver<GetAllLocationsResponse> responseObserver) {
        Page<LocationResponse> page = locationUseCase.getAllLocations(PageRequest.of(request.getPage(), request.getSize()));
        
        GetAllLocationsResponse response = GetAllLocationsResponse.newBuilder()
                .addAllLocations(page.getContent().stream().map(this::mapToGrpcResponse).collect(Collectors.toList()))
                .setTotalPages(page.getTotalPages())
                .setTotalElements(page.getTotalElements())
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void getLocationById(GetLocationByIdRequest request, StreamObserver<com.identio.mvp.interfaces.location.grpc.location.LocationResponse> responseObserver) {
        LocationResponse location = locationUseCase.getLocationById(UUID.fromString(request.getId()));
        responseObserver.onNext(mapToGrpcResponse(location));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void createLocation(com.identio.mvp.interfaces.location.grpc.location.CreateLocationRequest request, StreamObserver<com.identio.mvp.interfaces.location.grpc.location.LocationResponse> responseObserver) {
        CreateLocationRequest dto = new CreateLocationRequest(
                request.getName(),
                request.getAddress().isEmpty() ? null : request.getAddress(),
                request.getLatitude() == 0.0 ? null : request.getLatitude(),
                request.getLongitude() == 0.0 ? null : request.getLongitude(),
                parseJson(request.getMetadataJson())
        );
        LocationResponse created = locationUseCase.createLocation(dto);
        responseObserver.onNext(mapToGrpcResponse(created));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void updateLocation(com.identio.mvp.interfaces.location.grpc.location.UpdateLocationRequest request, StreamObserver<com.identio.mvp.interfaces.location.grpc.location.LocationResponse> responseObserver) {
        UpdateLocationRequest dto = new UpdateLocationRequest(
                request.getName().isEmpty() ? null : request.getName(),
                request.getAddress().isEmpty() ? null : request.getAddress(),
                request.getLatitude() == 0.0 ? null : request.getLatitude(),
                request.getLongitude() == 0.0 ? null : request.getLongitude(),
                parseJson(request.getMetadataJson())
        );
        LocationResponse updated = locationUseCase.updateLocation(UUID.fromString(request.getId()), dto);
        responseObserver.onNext(mapToGrpcResponse(updated));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLocation(DeleteLocationRequest request, StreamObserver<DeleteLocationResponse> responseObserver) {
        locationUseCase.deleteLocation(UUID.fromString(request.getId()));
        responseObserver.onNext(DeleteLocationResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }

    private com.identio.mvp.interfaces.location.grpc.location.LocationResponse mapToGrpcResponse(LocationResponse dto) {
        var builder = com.identio.mvp.interfaces.location.grpc.location.LocationResponse.newBuilder()
                .setId(dto.id().toString())
                .setName(dto.name());
        
        if (dto.address() != null) builder.setAddress(dto.address());
        if (dto.latitude() != null) builder.setLatitude(dto.latitude());
        if (dto.longitude() != null) builder.setLongitude(dto.longitude());
        if (dto.metadata() != null) builder.setMetadataJson(toJson(dto.metadata()));

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
