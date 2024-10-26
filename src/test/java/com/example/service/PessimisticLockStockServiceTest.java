package com.example.service;

import com.example.entity.Stock;
import com.example.repository.StockRepository;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PessimisticLockStockServiceTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PessimisticLockStockService pessimisticLockStockService;

    private Stock stock;

    @BeforeEach
    void init() {
        this.stock = this.stockRepository.saveAndFlush(new Stock(1L, 100));
    }

    @AfterEach
    void deleteAll() {
        this.stockRepository.deleteAll();
    }

    @Test
    void 비관적락_테스트() throws InterruptedException {
        Long id = this.stock.getId();
        int quantity = 1;

        int requestCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(requestCount);
        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                try {
                    this.pessimisticLockStockService.decreaseInventory(id, quantity);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock result = this.stockRepository.findById(id).orElseThrow();
        assertEquals(0, result.getQuantity());
    }

}