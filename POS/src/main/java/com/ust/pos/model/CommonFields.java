package com.ust.pos.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class CommonFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String identifier;
    private boolean status=true;
    private String modifiedBy;
    private String createdBy;
    private LocalDateTime modifiedOn;
    private LocalDateTime createdOn;
    private boolean deleted=false;
}
