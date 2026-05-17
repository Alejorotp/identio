package com.identio.mvp.domain.location.repositories;

import com.identio.mvp.domain.location.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, UUID> {
    List<Sensor> findByLocationId(UUID locationId);
    boolean existsByLocationId(UUID locationId);
}
