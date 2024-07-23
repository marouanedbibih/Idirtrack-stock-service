package com.idirtrack.stock_service.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;
@Repository
public interface StockRepository extends JpaRepository<Stock, Long>{
  
  List<Stock> findByDateEntree(Date dateEntree);

}
