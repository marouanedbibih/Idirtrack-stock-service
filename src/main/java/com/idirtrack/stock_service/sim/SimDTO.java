package com.idirtrack.stock_service.sim;

import com.idirtrack.stock_service.sim.SimStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimDTO {
    private Long id;
    private String pin;
    private String puk;
    private String ccid;
    private String operatorType;
    private SimStatus status;
    private String phoneNumber;
    private LocalDateTime addDate;
}
