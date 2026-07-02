package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("deleted = false")
@Entity
@Getter
@Setter
public class Brand extends CommonFields {
    private String icon;
}
