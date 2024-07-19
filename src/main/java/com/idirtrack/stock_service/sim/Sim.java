package com.idirtrack.stock_service.sim;

import com.idirtrack.stock_service.stock.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sim")
public class Sim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pin", nullable = false)
    private String pin;

    @Column(name = "puk", nullable = false)
    private String puk;

    @Column(name = "ccid", nullable = false, unique = true)
    private String ccid;

    @ManyToOne
    @JoinColumn(name = "sim_type_id")
    private SimType simType;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "add_date", nullable = false)
    private Date addDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SimStatus status;
}
