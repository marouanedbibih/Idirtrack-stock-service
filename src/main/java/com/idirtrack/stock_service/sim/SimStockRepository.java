package com.idirtrack.stock_service.sim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.idirtrack.stock_service.operator.Operator;
import com.idirtrack.stock_service.stock.Stock;

@Repository
public interface SimStockRepository extends JpaRepository<SimStock, Long> {

    SimStock findByStockAndOperator(Stock stock, Operator operator);

}
