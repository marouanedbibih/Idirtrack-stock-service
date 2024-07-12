package com.idirtrack.stock_service.device.https;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTypeRequeste {
  
  @NotBlank(message = "The name is required")
  private String name;
}
