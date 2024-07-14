package com.idirtrack.stock_service.sim;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    
        @Column(nullable = false, unique = true, length = 18)
        private String ccid;
    
        @Column(nullable = false)
        private String operatorType;
    
        @Enumerated(EnumType.STRING)
        private SimStatus status;
    
        @Column(nullable = false, unique = true)
        private String phoneNumber;
    
        @Column(nullable = false)
        private LocalDateTime addDate;
}
