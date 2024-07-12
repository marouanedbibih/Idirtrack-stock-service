package com.idirtrack.stock_service.device.https;

import com.idirtrack.stock_service.device.DeviceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "The Type is required")
    private String typeDevice;

    @NotNull(message = "The Status is required")
    private String status;
}
