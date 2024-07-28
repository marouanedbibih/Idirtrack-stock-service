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
    // Sim data
    private Long id;
    private String pin;
    private String puk;
    private String ccid;
    private String phone;
    private SimStatus status;

    // Time data
    private Date createdAt;
    private Date updatedAt;

    // Operator data
    private Long operatorId;
    private String operatorName;

    // Build form Entity to DTO
    // public static SimDTO fromDTO(Sim sim) {
    //     return SimDTO.builder()
    //             .id(sim.getId())
    //             .pin(sim.getPin())
    //             .puk(sim.getPuk())
    //             .ccid(sim.getCcid())
    //             .phone(sim.getPhone())
    //             .status(sim.getStatus())
    //             .createdAt(sim.getCreatedAt())
    //             .updatedAt(sim.getUpdatedAt())
    //             .operatorId(sim.getOperator().getId())
    //             .operatorName(sim.getOperator().getName())
    //             .build();
    // }   

    // Build Dto to Entity
    // public Sim toEntity() {
    //     return Sim.builder()
    //             .id(this.id)
    //             .pin(this.pin)
    //             .puk(this.puk)
    //             .ccid(this.ccid)
    //             .phone(this.phone)
    //             .status(this.status)
    //             .createdAt(this.createdAt)
    //             .updatedAt(this.updatedAt)
    //             .operator(Operator.builder().id(this.operatorId).name(this.operatorName).build())
    //             .build();
    // }


 
}
