package com.idirtrack.stock_service.sim.https;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimTypeRequest {

    @NotEmpty(message = "Type must not be empty")
    private String type;
}
