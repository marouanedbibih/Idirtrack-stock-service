package com.idirtrack.stock_service.sim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface SimRepository extends JpaRepository<Sim, Long>, JpaSpecificationExecutor<Sim> {

    @Query("SELECT s FROM Sim s WHERE s.pin LIKE %:query% OR s.puk LIKE %:query% OR s.ccid LIKE %:query% OR s.simType.type LIKE %:query% OR s.status LIKE %:query% OR s.phoneNumber LIKE %:query%")
    List<Sim> findByAnyFieldContaining(@Param("query") String query);

    @Query("SELECT s FROM Sim s WHERE s.addDate BETWEEN :startDate AND :endDate")
    List<Sim> findByAddDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    boolean existsByCcid(String ccid);  // Added method definition
}
