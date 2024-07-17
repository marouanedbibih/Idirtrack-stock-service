package com.idirtrack.stock_service.stock;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    // Save stock
    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    // Find stock by date
    public List<Stock> findByDateEntree(Date dateEntree) {
        return stockRepository.findByDateEntree(dateEntree);
    }

    // Get all stocks
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }
}
