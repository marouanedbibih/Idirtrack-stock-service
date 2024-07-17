package com.idirtrack.stock_service.stock;

import com.idirtrack.stock_service.basics.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stock-api")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/stocks")
    public ResponseEntity<BasicResponse> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.status(HttpStatus.OK).body(BasicResponse.builder()
                .data(stocks)
                .message("Stocks retrieved successfully")
                .status(HttpStatus.OK)
                .build());
    }
}
