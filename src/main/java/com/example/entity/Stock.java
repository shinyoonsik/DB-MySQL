package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;
    private int quantity;

    public Stock(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decreaseQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("재고 감소 주문은 0미만일 수 없다");
        if (this.quantity - quantity < 0) throw new RuntimeException("재고는 0미만일 수 없다");

        this.quantity -= quantity;
    }
}
