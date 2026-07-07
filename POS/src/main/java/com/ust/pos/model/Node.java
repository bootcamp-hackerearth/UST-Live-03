package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Getter
@Setter
@SQLRestriction("deleted = false")
public class Node extends CommonFields {
    private String path;
    private List<String> roles;
}
