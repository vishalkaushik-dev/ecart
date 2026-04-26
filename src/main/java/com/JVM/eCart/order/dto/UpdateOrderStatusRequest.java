package com.JVM.eCart.order.dto;

import com.JVM.eCart.order.enums.OrderStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(

        @NotNull(message = "Order product id is required")
        Long orderProductId,

        @NotNull(message = "Current Status is required")
        OrderStatusEnum fromStatus,

        @NotNull(message = "To Status is required")
        OrderStatusEnum toStatus,

        String notes
) { }
