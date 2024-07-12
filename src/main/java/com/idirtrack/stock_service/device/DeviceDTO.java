package com.idirtrack.stock_service.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceDTO {

    private Long id;
    private String IMEI;
    private DeviceStatus status;
    private String deviceType;
    private String remarque;
      
}
