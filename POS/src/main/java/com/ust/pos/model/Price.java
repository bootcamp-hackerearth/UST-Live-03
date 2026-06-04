package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Price extends CommonFields {
    private Long costPrice;
    private Long sellingPrice;
    private Long difference;
}