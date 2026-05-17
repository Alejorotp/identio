package com.identio.mvp.application.location.usecases;

import com.identio.mvp.application.location.dto.CreateLocationRequest;
import com.identio.mvp.application.location.dto.LocationResponse;
import com.identio.mvp.application.location.dto.UpdateLocationRequest;
import com.identio.mvp.domain.location.entities.Location;
import com.identio.mvp.domain.location.repositories.LocationRepository;
import com.identio.mvp.domain.location.repositories.SensorRepository;
import com.identio.mvp.interfaces.exceptions.BusinessValidationException;
import com.identio.mvp.interfaces.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationUseCaseImpl implements LocationUseCase {

    private final LocationRepository locationRepository;
    private final SensorRepository sensorRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<LocationResponse> getAllLocations(Pageable pageable) {
        return locationRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponse getLocationById(UUID id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        return mapToResponse(location);
    }

    @Override
    @Transactional
    public LocationResponse createLocation(CreateLocationRequest request) {
        Location location = Location.builder()
                .name(request.name())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .metadata(request.metadata())
                .build();
        locationRepository.save(location);
        return mapToResponse(location);
    }

    @Override
    @Transactional
    public LocationResponse updateLocation(UUID id, UpdateLocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        if (request.name() != null && !request.name().isBlank()) {
            location.setName(request.name());
        }
        if (request.address() != null) {
            location.setAddress(request.address());
        }
        if (request.latitude() != null) {
            location.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            location.setLongitude(request.longitude());
        }
        if (request.metadata() != null) {
            location.setMetadata(request.metadata());
        }

        locationRepository.save(location);
        return mapToResponse(location);
    }

    @Override
    @Transactional
    public void deleteLocation(UUID id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        
        if (sensorRepository.existsByLocationId(id)) {
            throw new BusinessValidationException("Cannot delete location because it has sensors attached.");
        }

        locationRepository.delete(location);
    }

    private LocationResponse mapToResponse(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude(),
                location.getMetadata()
        );
    }
}
