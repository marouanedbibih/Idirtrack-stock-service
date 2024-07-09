package com.idirtrack.stock_service.https.device;

import com.idirtrack.stock_service.model.device.DeviceType;

import ch.qos.logback.core.status.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceRequeste {
  
  @NotBlank(message = "The IMEI is required")
  @Size(min = 15, max = 15, message = "The IMEI must be 15 characters")
  private Number IMEI;
  @NotBlank(message = "The Type is required")
  private DeviceType type;
  
  @NotBlank(message = "The Status is required")
  private Status status;

  
}
