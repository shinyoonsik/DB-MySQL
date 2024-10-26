package com.example.service;

public class TransactionStockService {
    private final StockService stockService;

    public TransactionStockService(StockService stockService) {
        this.stockService = stockService;
    }

    public void descrease(Long id, int quantity){
        startTransaction();

        this.stockService.decreaseInventory(id, quantity);

        endTransaction();
    }

    private void startTransaction() {
        System.out.println("트랜잭션 시작!!!!!");
    }

    private void endTransaction() {
        System.out.println("트랜잭션 종료!!!!!");
    }
}
