package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Brand extends CommonFields {
    private String icon;
    private String description;
    private String iconPath;
}