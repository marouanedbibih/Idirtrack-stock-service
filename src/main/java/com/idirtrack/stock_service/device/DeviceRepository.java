package com.idirtrack.stock_service.device;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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


  
}
