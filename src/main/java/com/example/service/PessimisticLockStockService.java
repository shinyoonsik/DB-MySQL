package com.example.service;

import com.example.entity.Stock;
import com.example.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PessimisticLockStockService {

    private final StockRepository stockRepository;

    public PessimisticLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decreaseInventory(Long id, int quantity){
        String sessionId = this.stockRepository.getSessionId();
        System.out.println("sessionId = " + sessionId);
        Optional<Stock> optStock = this.stockRepository.findByIdWithPessimisticLock(id);

        if(optStock.isEmpty()) return;
        Stock stock = optStock.get();
        stock.decreaseQuantity(quantity);

//        this.stockRepository.saveAndFlush(stock);
    }
}
