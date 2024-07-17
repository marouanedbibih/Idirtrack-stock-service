//interface jpa repository for devicetype

package com.idirtrack.stock_service.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DeviceTypeRepository  extends JpaRepository<DeviceType, Long>{

  //device type already exists
  boolean existsByName(String name);
  Long findIdByName(String name);
  DeviceType findByName(String name);

  
}