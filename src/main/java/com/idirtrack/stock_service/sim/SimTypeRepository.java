package com.idirtrack.stock_service.sim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Date;

@Repository
public interface SimTypeRepository extends JpaRepository<SimType, Long> {
    boolean existsByType(String type);
    Optional<SimType> findByType(String type);
    Optional<SimType> findByTypeAndCreatedAt(String type, Date createdAt);
}
