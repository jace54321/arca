package com.arca.arca_backend.repository;

import com.arca.arca_backend.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {
    List<Device> findByUserIdOrderByLastActiveDesc(UUID userId);
    Optional<Device> findByUserIdAndDeviceName(UUID userId, String deviceName);
}
