package com.idirtrack.stock_service.sim;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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
    @JoinColumn(name = "sim_type_id", nullable = false)
    private SimType simType;
}
