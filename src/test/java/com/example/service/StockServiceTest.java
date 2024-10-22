package com.example.service;

import com.example.entity.Stock;
import com.example.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class StockServiceTest {

    @Autowired
    StockService stockService;

    @Autowired
    StockRepository stockRepository;

    Stock stock;

    @BeforeEach
    public void init() {
        // 1번 product의 재고가 100개임
        this.stock = this.stockRepository.saveAndFlush(new Stock(1L, 100));
    }

    @AfterEach
    public void deleteAll(){
        this.stockRepository.deleteAll();
    }

    @Test
    public void 재고감소_with_싱글스레드() {
        Long id = this.stock.getId();
        int quantity = 1;

        // 100 - 1 = 99
        this.stockService.decreaseInventory(id, quantity);

        Stock stock = this.stockRepository.findById(id).orElseThrow();
        assertEquals(99, stock.getQuantity());
    }

    @Test
    public void 동시에_100개의_요청_테스트_with_멀티스레드() throws InterruptedException {
        Long id = this.stock.getId();
        int quantity = 1;

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            // 스레드풀에 자업을 제출한다. 스레드 풀의 크기가 20이므로 20개의 스레드만 동시에 실행되고 나머지 작업은 대기 큐(BlockingQueue)에 저장된다.
            // 작업이 완료되면 대기 중인 작업이 하나씩 실행된다
            // for문이 발행자 역할을 하고 스레드 풀은 구독자처럼 동작한다(like pub/sub패턴)
            // 결과적으로 for문이 작업을 다 제출한 후에도 작업이 처리되는 과정은 계속 된다.
            // submit(): non-blocking 호출이며 내부적으로 작업이 비동기적으로 처리된다.
            executorService.submit(() -> {
                try {
                    this.stockService.decreaseInventory(id, quantity);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        /**
         * 예상 시나리오, 100번 decrease요청 -> quantity = 0 이어야 하지만, 결과는 0이 아님
         *
         * 테스트 실패!
         * why, Race condtion발생
         * sol) 공유자원을 먼저 선점한 스레드가 작업을 마친 이후에 다른 스레드가 공유자원을 가지게 하면 된다!
         */
        Stock stock = this.stockRepository.findById(id).orElseThrow();
        assertEquals(0, stock.getQuantity());
    }

}