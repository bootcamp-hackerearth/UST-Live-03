package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLRestriction("deleted = false")
public class Customer extends CommonFields{
    private String email;
    private String address;
    private Long phoneno;
    private String partytype;
}
