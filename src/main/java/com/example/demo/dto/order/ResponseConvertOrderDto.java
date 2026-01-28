package com.example.demo.dto.order;

import com.example.demo.dto.product.ProductDto;

import java.util.List;

public record ResponseConvertOrderDto(
        String warehouseOrderNumber,
        List<ProductDto> productList,
        String shipBy
) {
}
