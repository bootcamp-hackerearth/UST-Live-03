package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Getter
@Setter
@Entity
@SQLRestriction("deleted = false")
public class User extends CommonFields {
    private String username;
    private String name;
    private String phoneNo;
    private List<String> roles;
    private String password;
}
