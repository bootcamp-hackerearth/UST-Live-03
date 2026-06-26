package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_entry")
public class OrderEntry extends CommonFields {
    private String orderId;
    private String product;
    private Integer quantity;
    private Double unitPrice;
    private Double discount;
    private Double totalPrice;
}