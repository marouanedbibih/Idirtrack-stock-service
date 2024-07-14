package com.idirtrack.stock_service.sim;

import com.idirtrack.stock_service.sim.Sim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SimRepository extends JpaRepository<Sim, Long> {

    @Query("SELECT s FROM Sim s WHERE s.pin LIKE %:query% OR s.puk LIKE %:query% OR s.ccid LIKE %:query% OR s.operatorType LIKE %:query% OR s.status LIKE %:query% OR s.phoneNumber LIKE %:query%")
    List<Sim> findByAnyFieldContaining(@Param("query") String query);

    @Query("SELECT s FROM Sim s WHERE s.addDate BETWEEN :startDate AND :endDate")
    List<Sim> findByAddDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
