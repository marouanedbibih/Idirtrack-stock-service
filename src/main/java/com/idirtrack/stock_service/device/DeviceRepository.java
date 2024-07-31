package com.idirtrack.stock_service.device;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // Add this import


@Repository
public interface DeviceRepository  extends JpaRepository<Device, Long>{
  
  //IMEI already exists
  boolean existsByImei(String imei);

  //get all device by pagination

  @Query("SELECT d FROM Device d ORDER BY d.id DESC")
  Page<Device> findAllByOrderByIdDesc(Pageable pageable);

  //search device 

  Page<Device> findAll(Specification<Device> specification, Pageable pageable);

  //count device have status non install
  Long countByStatus(DeviceStatus status);

  //get all device by status non-installed and pagination
  Page<Device> findAllByStatus(DeviceStatus status, Pageable pageable);

  //search device  non installed  by imei
   @Query("SELECT d FROM Device d WHERE d.status = :status AND d.imei LIKE %:imei%")
    Page<Device> findAllByStatusAndImeiContaining(@Param("status") DeviceStatus status, @Param("imei") String imei, Pageable pageable);

    // Check if the device type already exists by id
    boolean existsById(Long id);

    //search device by imei
    Page<Device> findByImeiContaining(String imei, Pageable pageable);

  
}
