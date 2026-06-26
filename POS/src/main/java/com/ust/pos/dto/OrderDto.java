package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto extends CommonDto {
    private BigDecimal totalPrice;
    private String couponCode;
    private BigDecimal totalDiscount;
    private String orderId;
    private String paymentMode;
    private LocalDateTime orderDate;
    private String orderStatus;
    private List<OrderEntryDto> entryDtoList;
}