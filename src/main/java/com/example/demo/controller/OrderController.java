package com.example.demo.controller;

import com.example.demo.dto.order.RequestOrderDto;
import com.example.demo.dto.order.ResponseConvertOrderDto;
import com.example.demo.service.convertedorder.ConvertedOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/orders")
public class OrderController {
    private final ConvertedOrderService orderService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/convert")
    public ResponseConvertOrderDto convertOrder(@Valid @RequestBody RequestOrderDto orderDto) {
        return orderService.convertOrder(orderDto);
    }
}
