package com.ust.pos.modell;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends CommonFields {
    private String username;
    private String name;
    private String phoneNo;
    private List<String> roles;
    private String password;
}