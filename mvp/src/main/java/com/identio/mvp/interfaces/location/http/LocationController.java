package com.identio.mvp.interfaces.location.http;

import com.identio.mvp.application.location.dto.CreateLocationRequest;
import com.identio.mvp.application.location.dto.LocationResponse;
import com.identio.mvp.application.location.dto.UpdateLocationRequest;
import com.identio.mvp.application.location.usecases.LocationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationUseCase locationUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<LocationResponse>> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(locationUseCase.getAllLocations(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable UUID id) {
        return ResponseEntity.ok(locationUseCase.getLocationById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationResponse> createLocation(@RequestBody CreateLocationRequest request) {
        return ResponseEntity.ok(locationUseCase.createLocation(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable UUID id,
            @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(locationUseCase.updateLocation(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
        locationUseCase.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
