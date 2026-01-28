package com.example.demo.service.convertedorder;

import com.example.demo.dto.order.RequestOrderDto;
import com.example.demo.dto.order.ResponseConvertOrderDto;
import com.example.demo.dto.product.ProductDto;
import com.example.demo.exception.DataValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConvertedOrderServiceImpl implements ConvertedOrderService {
    private final String ORDER_PREFIX = "WH-";
    private final int MAX_SHIP_DAY = 30;

    @Override
    public ResponseConvertOrderDto convertOrder(RequestOrderDto orderDto) {
        String warehouseOrderNumber = prepareWarehouseOrderNumber(orderDto);
        List<ProductDto> productList = prepareProductList(orderDto);
        String shipBy = prepareDate(orderDto);

        return new ResponseConvertOrderDto(warehouseOrderNumber, productList, shipBy);
    }

    private String formattedSku(String sku) {
        int skuLength = sku.length();

        String firstPart = skuLength >= 3 ? sku.substring(0, 3).toUpperCase() : sku.toUpperCase();
        String lastPart = skuLength >= 5 ? sku.substring(skuLength - 2).toLowerCase() : "";

        return firstPart + lastPart;
    }

    private String prepareWarehouseOrderNumber(RequestOrderDto orderDto) {
        String[] orderArrInfo = orderDto.orderId().split("-");

        if (orderArrInfo.length != 3
                || orderArrInfo[0].isEmpty()
                || orderArrInfo[1].isEmpty()
                || orderArrInfo[2].isEmpty()
        ) {
            throw new DataValidationException("orderId должен быть в формате ORD-2024-12345");
        }

        return ORDER_PREFIX + orderArrInfo[2];
    }

    private List<ProductDto> prepareProductList(RequestOrderDto orderDto) {
        String[] productItems = orderDto.items().split(",");

        if (productItems.length < 1) {
            throw new DataValidationException("items должен содержать хотя бы один товар");
        }

        List<ProductDto> productList = new ArrayList<>();

        for (String item : productItems) {
            String[] productInfo = item.split("x");

            if (productInfo.length != 2) {
                throw new DataValidationException("Ошибка парсинга товара");
            }

            int quantity;
            try {
                quantity = Integer.parseInt(productInfo[0]);
            } catch (NumberFormatException e) {
                throw new DataValidationException("Количество товара должно быть целым числом");
            }

            if (quantity <= 0) {
                throw new DataValidationException("Количество товара должно быть больше 0");
            }

            String sku = formattedSku(productInfo[1]);

            ProductDto productDto = new ProductDto(sku, quantity);
            productList.add(productDto);
        }

        return productList;
    }

    private String prepareDate(RequestOrderDto orderDto) {
        LocalDate currentTime = LocalDate.now();
        LocalDate shipBy = orderDto.deliveryDate().toLocalDate();

        if (shipBy.isBefore(currentTime)) {
            throw new DataValidationException("Дата не должна быть меньше текущей");
        }

        LocalDate maxDate = currentTime.plusDays(MAX_SHIP_DAY);

        if (shipBy.isAfter(maxDate)) {
            shipBy = LocalDate.from(currentTime.plusDays(MAX_SHIP_DAY).atStartOfDay());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return shipBy.format(formatter);
    }
}
