package com.idirtrack.stock_service.sim;

import java.time.LocalDateTime;

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
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sim")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false)
    private String puk;

    @Column(nullable = false, length = 18)
    private String ccid;

    @Enumerated(EnumType.STRING)
    private SimStatus status;

    private String phoneNumber;

    private LocalDateTime addDate;

    @ManyToOne
    @JoinColumn(name = "sim_type_name", nullable = false)
    private SimType simType;

    @Transient
    public String getOperatorType() {
        return this.simType != null ? this.simType.getType() : null;
    }
}
