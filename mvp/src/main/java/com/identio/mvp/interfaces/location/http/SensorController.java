package com.identio.mvp.interfaces.location.http;

import com.identio.mvp.application.location.dto.CreateSensorRequest;
import com.identio.mvp.application.location.dto.SensorResponse;
import com.identio.mvp.application.location.dto.UpdateSensorRequest;
import com.identio.mvp.application.location.dto.UpdateSensorStatusRequest;
import com.identio.mvp.application.location.usecases.SensorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorUseCase sensorUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SensorResponse>> getAllSensors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(sensorUseCase.getAllSensors(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SensorResponse> getSensorById(@PathVariable UUID id) {
        return ResponseEntity.ok(sensorUseCase.getSensorById(id));
    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SensorResponse>> getSensorsByLocation(@PathVariable UUID locationId) {
        return ResponseEntity.ok(sensorUseCase.getSensorsByLocation(locationId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SensorResponse> createSensor(@RequestBody CreateSensorRequest request) {
        return ResponseEntity.ok(sensorUseCase.createSensor(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SensorResponse> updateSensor(
            @PathVariable UUID id,
            @RequestBody UpdateSensorRequest request) {
        return ResponseEntity.ok(sensorUseCase.updateSensor(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SensorResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateSensorStatusRequest request) {
        return ResponseEntity.ok(sensorUseCase.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSensor(@PathVariable UUID id) {
        sensorUseCase.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }
}
