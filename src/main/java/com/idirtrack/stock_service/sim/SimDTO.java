package com.idirtrack.stock_service.sim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimDTO {
    private Long id;
    private String pin;
    private String puk;
    private String ccid;
    private String simType; // Operator name like SFR, INWI, ORANGE
    private SimStatus status;
    private String phoneNumber;
    private Date addDate;
}
