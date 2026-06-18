package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderRequestDto {

    private String cartIdentifier;
    private String paymentMode;
}