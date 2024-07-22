package com.idirtrack.stock_service.sim.https;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimTypeRequest {
    @NotBlank(message = "The type field must not be empty")
    private String type;
}

