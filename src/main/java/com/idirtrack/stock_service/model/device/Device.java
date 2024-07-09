package com.idirtrack.stock_service.model.device;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device")
public class Device { 

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String IMEI;
  private Date createdAt; // Changed create_at to createdAt
  private Status status; // Ensure Status is defined or imported

}
