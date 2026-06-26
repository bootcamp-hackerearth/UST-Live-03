package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderDto extends CommonDto {

    private String customerEmail;

    private String shippingAddress;

    private String paymentMethod;

    private LocalDateTime paymentTime;

    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal total;

    private String cartId;
}