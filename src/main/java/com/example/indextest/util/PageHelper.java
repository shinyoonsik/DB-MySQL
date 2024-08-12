package com.example.indextest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.springframework.data.domain.Sort.Order;

@Slf4j
public class PageHelper {
    public static String orderBy(Sort sort){
        if(sort.isEmpty()) return "id DESC";

        List<Order> orders = sort.toList();
        List<String> modifiedOrders = orders.stream()
                .map(order -> order.getProperty() + " " + order.getDirection())
                .toList();

        String orderStr = String.join(", ", modifiedOrders);
        log.info(orderStr);
        return orderStr;
    }
}
