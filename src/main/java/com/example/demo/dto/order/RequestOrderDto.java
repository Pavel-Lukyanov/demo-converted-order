package com.example.demo.dto.order;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RequestOrderDto(
        @NotNull(message = "Идентификатор заказа не должен быть пустым")
        String orderId,
        @NotNull(message = "Поле items обяхательно для заполнения")
        String items,
        @NotNull(message = "Дата доставки обязательна для заполнения")
        LocalDateTime deliveryDate
) {
}
