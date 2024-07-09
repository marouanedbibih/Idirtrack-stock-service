package com.idirtrack.stock_service.service.device;

import com.idirtrack.stock_service.repository.device.DeviceRepository;
import com.idirtrack.stock_service.repository.device.DeviceTypeRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceServie {

  private final DeviceRepository deviceRepository;
  private final DeviceTypeRepository deviceTypeRepository;

  //save device
  
 
}
