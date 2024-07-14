package com.idirtrack.stock_service.sim.https;

import java.time.LocalDateTime;

import com.idirtrack.stock_service.sim.SimStatus;

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
public class SimUpdateRequest {

    @NotBlank(message = "The PIN is required")
    @Size(min = 4, max = 8, message = "The PIN must be between 4 and 8 characters")
    private String pin;

    @NotBlank(message = "The PUK is required")
    @Size(min = 8, max = 12, message = "The PUK must be between 8 and 12 characters")
    private String puk;

    @NotBlank(message = "The CCID is required")
    @Pattern(regexp = "\\d{19,20}", message = "The CCID must be between 19 and 20 digits")
    private String ccid;

    @NotNull(message = "The Operator Type is required")
    private String operatorType;

    @NotBlank(message = "The Phone Number is required")
    @Pattern(regexp = "\\d{10,15}", message = "The Phone Number must be between 10 and 15 digits")
    private String phoneNumber;

    @NotNull(message = "The Add Date is required")
    private LocalDateTime addDate;

    private SimStatus status;
}
