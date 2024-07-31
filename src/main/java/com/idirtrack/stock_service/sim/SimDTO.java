package com.idirtrack.stock_service.sim;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimDTO {
    private Long id;
    private String pin;
    private String puk;
    private String ccid;
    private String phone;
    private SimStatus status;
    private Date createdAt;
    private Date updatedAt;
    private Long operatorId;
    private String operatorName;
}
