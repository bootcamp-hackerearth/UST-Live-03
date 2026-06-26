package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderDto extends CommonDto {
    private String customer;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private String orderId;
    private String couponCode;
    private String orderStatus;
    private List<OrderEntryDto> orderEntryDtoList;
}