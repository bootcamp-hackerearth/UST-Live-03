package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrdersDto extends CommonDto {

    private List<OrderEntryDto> orderEntryDtoList;

    private BigDecimal totalPrice;
    private BigDecimal totalOriginalPrice;
    private BigDecimal discount;

    private String coupon;
    private String couponCode;

    private String paymentType;
    private String customerId;
}
