package com.idirtrack.stock_service.sim;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "sim_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String type;

    @OneToMany(mappedBy = "simType")
    private List<Sim> sims;
}
