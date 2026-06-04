package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stocks extends CommonFields {

    private Long availableStock;
    private Long incomingStock;
    private Long outgoingStock;
    private String productStatus;
    private String wareHouse;
    private String skuCode;

}
