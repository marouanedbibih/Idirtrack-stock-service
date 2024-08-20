package com.idirtrack.stock_service.operator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OperatorDTO {
    private Long id;
    private String name;
    private int totalSims;
}
