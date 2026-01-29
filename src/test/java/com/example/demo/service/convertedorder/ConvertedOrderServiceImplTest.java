package com.example.demo.service.convertedorder;

import com.example.demo.dto.order.RequestOrderDto;
import com.example.demo.dto.order.ResponseConvertOrderDto;
import com.example.demo.dto.product.ProductDto;
import com.example.demo.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConvertedOrderServiceImplTest {

    private Clock fixedClock;
    private ConvertedOrderServiceImpl convertedOrderService;

    @BeforeEach
    void setUp() throws Exception {
        fixedClock = Clock.fixed(
                Instant.parse("2026-01-29T00:00:00Z"),
                ZoneId.of("UTC")
        );

        convertedOrderService = new ConvertedOrderServiceImpl(fixedClock);

        Field maxShipDayField = ConvertedOrderServiceImpl.class
                .getDeclaredField("maxShipDay");
        maxShipDayField.setAccessible(true);
        maxShipDayField.set(convertedOrderService, 30);
    }

    @Test
    void convertOrder_shouldConvertOrderSuccessfully_whenRequestIsValid() {
        LocalDateTime deliveryDate = LocalDate.now(fixedClock).plusDays(5).atStartOfDay();

        RequestOrderDto requestOrderDto = new RequestOrderDto(
                "ORD-2024-12345",
                "3xAPPLE,2xBANANA,1xORANGE",
                deliveryDate
        );

        List<ProductDto> productList = List.of(
                new ProductDto("APPle", 3),
                new ProductDto("BANna", 2),
                new ProductDto("ORAge", 1)
        );

        String expectedShipBy = deliveryDate.toLocalDate()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        ResponseConvertOrderDto responseConvertOrderDto = new ResponseConvertOrderDto(
                "WH-12345",
                productList,
                expectedShipBy
        );

        ResponseConvertOrderDto result = convertedOrderService.convertOrder(requestOrderDto);

        assertEquals(responseConvertOrderDto.warehouseOrderNumber(), result.warehouseOrderNumber());
        assertEquals(responseConvertOrderDto.shipBy(), result.shipBy());
        assertEquals(responseConvertOrderDto.productList().size(), result.productList().size());

        for (int i = 0; i < result.productList().size(); i++) {
            assertEquals(responseConvertOrderDto.productList().get(i).sku(), result.productList().get(i).sku());
            assertEquals(responseConvertOrderDto.productList().get(i).quantity(), result.productList().get(i).quantity());
        }
    }

    @Test
    void convertOrder_shouldThrowDataValidateException_whenDeliveryDatePassed() {
        LocalDateTime deliveryDate = LocalDateTime.now().minusDays(5);

        RequestOrderDto requestOrderDto = new RequestOrderDto(
                "ORD-2024-12345",
                "3xAPPLE,2xBANANA,1xORANGE",
                deliveryDate
        );

        assertThrows(DataValidationException.class, () ->
                convertedOrderService.convertOrder(requestOrderDto)
        );
    }

    @Test
    void convertOrder_shouldConvertOrderSuccessfully_whenDeliveryDateMoreThanDays() {
        LocalDateTime deliveryDate = LocalDate.now(fixedClock).plusDays(45).atStartOfDay();

        RequestOrderDto requestOrderDto = new RequestOrderDto(
                "ORD-2024-12345",
                "3xAPPLE,2xBANANA,1xORANGE",
                deliveryDate
        );

        List<ProductDto> productList = List.of(
                new ProductDto("APPle", 3),
                new ProductDto("BANna", 2),
                new ProductDto("ORAge", 1)
        );

        String expectedShipBy = LocalDate.now(fixedClock).plusDays(30)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        ResponseConvertOrderDto responseConvertOrderDto = new ResponseConvertOrderDto(
                "WH-12345",
                productList,
                expectedShipBy
        );

        ResponseConvertOrderDto result = convertedOrderService.convertOrder(requestOrderDto);

        assertEquals(responseConvertOrderDto.warehouseOrderNumber(), result.warehouseOrderNumber());
        assertEquals(responseConvertOrderDto.shipBy(), result.shipBy());
        assertEquals(responseConvertOrderDto.productList().size(), result.productList().size());

        for (int i = 0; i < result.productList().size(); i++) {
            assertEquals(responseConvertOrderDto.productList().get(i).sku(), result.productList().get(i).sku());
            assertEquals(responseConvertOrderDto.productList().get(i).quantity(), result.productList().get(i).quantity());
        }
    }
}