package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderDto extends CommonDto {

    private String invoiceCode;
    private String customerIdentifier;
    private String warehouseIdentifier;

    private BigDecimal originalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal totalPrice;

    private BigDecimal amountReceived;
    private BigDecimal changeAmount;
    private BigDecimal dueAmount;

    private String paymentMethod;
    private String transactionNotes;
    private String couponCode;

    private List<OrderEntryDto> entryList;
}