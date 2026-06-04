package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Stock extends CommonFields {
    private Long quantity;
    private Boolean status;
    private String product;
    private String warehouse;

}
