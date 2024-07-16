package com.idirtrack.stock_service.sim.https;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimRequest {

    @NotBlank(message = "The PIN is required")
    @Size(min = 4, max = 8, message = "The PIN must be between 4 and 8 characters")
    private String pin;

    @NotBlank(message = "The PUK is required")
    @Pattern(regexp = "\\d{8}", message = "The PUK must be exactly 8 digits")
    private String puk;

    @NotBlank(message = "The CCID is required")
    @Pattern(regexp = "\\d{8}", message = "The CCID must be at most 18 characters")
    private String ccid;

    @NotNull(message = "The SIM type ID is required")
    private String simType;

    @NotBlank(message = "The Phone Number is required")
    @Pattern(regexp = "\\d{10,15}", message = "The Phone Number must be between 10 and 15 digits")
    private String phoneNumber;

    
}