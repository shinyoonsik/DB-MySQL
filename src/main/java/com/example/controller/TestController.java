package com.example.controller;

import com.example.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final StockService stockService;

    @GetMapping(path = "/test1")
    public String test1(){
        var str =  "test1이다";
        stockService.decreaseInventory(1L, 10);
        System.out.println(str);
        return str;
    }

}
