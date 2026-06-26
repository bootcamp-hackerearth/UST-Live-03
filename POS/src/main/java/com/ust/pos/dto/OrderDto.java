package com.ust.pos.dto;

import com.ust.pos.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto extends CommonDto {

    private String customerId;

    private BigDecimal subtotal;

    private String paymentMethod;

    private Boolean paymentCompleted;

    private BigDecimal discount;

    private BigDecimal totalPrice;

    private String couponCode;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private List<OrderItemDto> items;

    private String customer;
}