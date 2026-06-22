package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto extends CommonDto {

    private String orderId;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private String couponCode;
    private String paymentMode;
    private LocalDateTime orderDate;
    private List<OrderEntryDto> entryDtoList;
}