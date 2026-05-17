package com.identio.mvp.application.location.usecases;

import com.identio.mvp.application.location.dto.CreateSensorRequest;
import com.identio.mvp.application.location.dto.SensorResponse;
import com.identio.mvp.application.location.dto.UpdateSensorRequest;
import com.identio.mvp.application.location.dto.UpdateSensorStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SensorUseCase {
    Page<SensorResponse> getAllSensors(Pageable pageable);
    SensorResponse getSensorById(UUID id);
    List<SensorResponse> getSensorsByLocation(UUID locationId);
    SensorResponse createSensor(CreateSensorRequest request);
    SensorResponse updateSensor(UUID id, UpdateSensorRequest request);
    SensorResponse updateStatus(UUID id, UpdateSensorStatusRequest request);
    void deleteSensor(UUID id);
}
