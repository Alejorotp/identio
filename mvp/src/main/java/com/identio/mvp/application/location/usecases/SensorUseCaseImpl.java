package com.identio.mvp.application.location.usecases;

import com.identio.mvp.application.location.dto.CreateSensorRequest;
import com.identio.mvp.application.location.dto.SensorResponse;
import com.identio.mvp.application.location.dto.UpdateSensorRequest;
import com.identio.mvp.application.location.dto.UpdateSensorStatusRequest;
import com.identio.mvp.domain.location.entities.Location;
import com.identio.mvp.domain.location.entities.Sensor;
import com.identio.mvp.domain.location.repositories.LocationRepository;
import com.identio.mvp.domain.location.repositories.SensorRepository;
import com.identio.mvp.interfaces.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorUseCaseImpl implements SensorUseCase {

    private final SensorRepository sensorRepository;
    private final LocationRepository locationRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<SensorResponse> getAllSensors(Pageable pageable) {
        return sensorRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SensorResponse getSensorById(UUID id) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found"));
        return mapToResponse(sensor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SensorResponse> getSensorsByLocation(UUID locationId) {
        return sensorRepository.findByLocationId(locationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SensorResponse createSensor(CreateSensorRequest request) {
        Location location = null;
        if (request.locationId() != null) {
            location = locationRepository.findById(request.locationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        }

        Sensor sensor = Sensor.builder()
                .name(request.name())
                .macAddress(request.macAddress())
                .status(request.status())
                .location(location)
                .config(request.config())
                .build();
        
        sensorRepository.save(sensor);
        return mapToResponse(sensor);
    }

    @Override
    @Transactional
    public SensorResponse updateSensor(UUID id, UpdateSensorRequest request) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found"));

        if (request.name() != null && !request.name().isBlank()) {
            sensor.setName(request.name());
        }
        if (request.locationId() != null) {
            Location location = locationRepository.findById(request.locationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
            sensor.setLocation(location);
        }
        if (request.config() != null) {
            sensor.setConfig(request.config());
        }

        sensorRepository.save(sensor);
        return mapToResponse(sensor);
    }

    @Override
    @Transactional
    public SensorResponse updateStatus(UUID id, UpdateSensorStatusRequest request) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found"));
        
        sensor.setStatus(request.status());
        sensorRepository.save(sensor);
        return mapToResponse(sensor);
    }

    @Override
    @Transactional
    public void deleteSensor(UUID id) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found"));
        sensorRepository.delete(sensor);
    }

    private SensorResponse mapToResponse(Sensor sensor) {
        return new SensorResponse(
                sensor.getId(),
                sensor.getName(),
                sensor.getMacAddress(),
                sensor.getStatus(),
                sensor.getLocation() != null ? sensor.getLocation().getId() : null,
                sensor.getConfig()
        );
    }
}
