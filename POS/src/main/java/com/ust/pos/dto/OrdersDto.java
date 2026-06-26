package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrdersDto extends CommonDto {

    private String customerId;

    private String customerName;

    private Long customerPhone;

    private BigDecimal totalOriginalPrice;

    private BigDecimal discount;

    private BigDecimal totalPrice;

    private String couponCode;

    private String paymentType;

    private LocalDateTime orderDate;

    private String status;

    private List<OrdersEntryDto> ordersEntryDtoList;
}