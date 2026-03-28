package com.arca.arca_backend.repository;

import com.arca.arca_backend.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SyncLogRepository extends JpaRepository<SyncLog, UUID> {
    List<SyncLog> findByUserIdOrderByTimestampDesc(UUID userId);
}
