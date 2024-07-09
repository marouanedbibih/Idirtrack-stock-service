package com.idirtrack.stock_service.repository.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.idirtrack.stock_service.model.device.Device;

@Repository
public interface DeviceRepository  extends JpaRepository<Device, Long>{
  
  
}
