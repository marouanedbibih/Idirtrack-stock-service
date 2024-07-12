package com.idirtrack.stock_service.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository  extends JpaRepository<Device, Long>{
  
  //IMEI already exists
  @Query("SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END FROM Device d WHERE d.IMEI = :IMEI")
  boolean existsByImei(Number IMEI);
  
}
