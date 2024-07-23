package com.idirtrack.stock_service.sim;

import com.idirtrack.stock_service.stock.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sim_stock")
public class SimStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sim_type_id")
    private SimType simType;

    @OneToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
}
