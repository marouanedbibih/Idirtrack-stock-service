package com.idirtrack.stock_service.sim;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "add_date")
    @Temporal(TemporalType.DATE)
    private Date addDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SimStatus status;
}
