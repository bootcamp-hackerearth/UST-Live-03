package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrdersDto extends CommonDto{
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private String coupon;
    private BigDecimal totalOriginalPrice;
    private LocalDateTime orderDate = LocalDateTime.now();
    private String paymentType;
    private String customerId;
}
