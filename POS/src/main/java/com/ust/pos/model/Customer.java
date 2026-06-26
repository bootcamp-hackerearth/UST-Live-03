package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends CommonFields{
    private String email;
    private String address;
    private Long phoneno;
    private String partytype;
}
