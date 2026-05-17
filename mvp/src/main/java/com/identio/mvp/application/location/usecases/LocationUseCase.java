package com.identio.mvp.application.location.usecases;

import com.identio.mvp.application.location.dto.CreateLocationRequest;
import com.identio.mvp.application.location.dto.LocationResponse;
import com.identio.mvp.application.location.dto.UpdateLocationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LocationUseCase {
    Page<LocationResponse> getAllLocations(Pageable pageable);
    LocationResponse getLocationById(UUID id);
    LocationResponse createLocation(CreateLocationRequest request);
    LocationResponse updateLocation(UUID id, UpdateLocationRequest request);
    void deleteLocation(UUID id);
}
