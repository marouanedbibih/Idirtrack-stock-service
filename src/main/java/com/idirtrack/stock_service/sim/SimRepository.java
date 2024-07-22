package com.idirtrack.stock_service.sim;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SimRepository extends JpaRepository<Sim, Long>, JpaSpecificationExecutor<Sim> {

    @Query("SELECT s FROM Sim s WHERE s.pin LIKE %:query% OR s.puk LIKE %:query% OR s.ccid LIKE %:query% OR s.simType.type LIKE %:query% OR s.status LIKE %:query% OR s.phoneNumber LIKE %:query%")
    List<Sim> findByAnyFieldContaining(@Param("query") String query);

    @Query("SELECT s FROM Sim s WHERE s.addDate BETWEEN :startDate AND :endDate")
    List<Sim> findByAddDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    boolean existsByCcid(String ccid);

    Long countByStatus(SimStatus status);

    Page<Sim> findAllByStatus(SimStatus status, Pageable pageable);

    @Query("SELECT COUNT(s) > 0 FROM Sim s WHERE s.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT s FROM Sim s WHERE s.status = :status AND (s.phoneNumber LIKE CONCAT('%',:query,'%') OR s.ccid LIKE CONCAT('%',:query,'%'))")
    Page<Sim> findAllByStatusAndPhoneNumberContainingOrCcidContaining(
            @Param("status") SimStatus status,
            @Param("query") String query,
            Pageable pageable);
}
