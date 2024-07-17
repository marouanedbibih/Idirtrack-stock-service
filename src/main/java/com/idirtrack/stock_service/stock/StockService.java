package com.idirtrack.stock_service.stock;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class StockService {
  
  private final StockRepository stockRepository;

  //save stock
  public Stock saveStock(Stock stock) {
    return stockRepository.save(stock);
  }
 


}
