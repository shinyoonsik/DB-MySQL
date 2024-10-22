package com.example.service;

import com.example.entity.Stock;
import com.example.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

//    @Transactional
    public synchronized void decreaseInventory(Long id, int quantity){
        Stock stock = this.stockRepository.findById(id).orElseThrow(() -> new RuntimeException(format("%d번 재고가 없다", id)));

        stock.decreaseQuantity(quantity);

        System.out.println("---------------");

        this.stockRepository.saveAndFlush(stock);
    }
}
