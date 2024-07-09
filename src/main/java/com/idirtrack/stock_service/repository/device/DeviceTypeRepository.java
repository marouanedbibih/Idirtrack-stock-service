//interface jpa repository for devicetype

package com.idirtrack.stock_service.repository.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.idirtrack.stock_service.model.device.DeviceType;

@Repository
public interface DeviceTypeRepository  extends JpaRepository<DeviceType, Long>{
  
}