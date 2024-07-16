package com.idirtrack.stock_service.sim.https;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimUpdateRequest {

    @NotBlank(message = "The PIN is required")
    private String pin;

    @NotBlank(message = "The PUK is required")
    @Pattern(regexp = "\\d{8}", message = "The PUK must be 8 digits")
    private String puk;

    @NotBlank(message = "The CCID is required")
    @Size(max = 18, message = "The CCID must be less than or equal to 18 characters")
    private String ccid;

    @NotNull(message = "The SIM type by operator name is required")
    private String simType;

    @NotBlank(message = "The phone number is required")
    private String phoneNumber;

    @NotBlank(message = "The status is required")
    private String status;

}
