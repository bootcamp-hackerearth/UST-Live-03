package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Setter
@Getter
@SQLRestriction("deleted = false")
public class Category extends CommonFields{
    private String superCategory;
}
