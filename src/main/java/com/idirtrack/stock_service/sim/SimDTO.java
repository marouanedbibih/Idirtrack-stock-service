package com.idirtrack.stock_service.sim;

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
    private String simType;
    private SimStatus status;
    private String phoneNumber;
    private java.sql.Date addDate;
    // Add this field
}
