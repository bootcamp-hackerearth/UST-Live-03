package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter

public class Order extends CommonFields {
    private String customer;
    private String customerName;
    private Long customerPhone;
    private Double totalOriginalPrice;
    private Double discount;
    private Double totalPrice;
    private LocalDateTime orderDate;
    private String status;
}
