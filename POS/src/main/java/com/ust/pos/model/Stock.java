package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLRestriction("deleted = false")
public class Stock extends CommonFields{
    private String warehouseName;
    private Long quantity;
}
