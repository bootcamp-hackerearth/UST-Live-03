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
    private String cartIdentifier;
    private String customerIdentifier;
    private CustomerDto customer;
    private String paymentMode;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;
    private String coupon;
    private List<OrderEntryDto> orderEntries;
}