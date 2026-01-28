package com.example.demo.service.convertedorder;

import com.example.demo.dto.order.RequestOrderDto;
import com.example.demo.dto.order.ResponseConvertOrderDto;

public interface ConvertedOrderService {
    ResponseConvertOrderDto convertOrder(RequestOrderDto orderDto);
}
