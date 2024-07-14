package com.idirtrack.stock_service.sim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimTypeRepository extends JpaRepository<SimType, Long> {
    boolean existsByType(String type);
}